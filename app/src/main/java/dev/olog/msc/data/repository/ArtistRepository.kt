package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.data.FileUtils
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.img.ImagesFolderUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

@Singleton
class ArtistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val albumGateway: AlbumGateway,
        appDatabase: AppDatabase,
        imagesCreator: ImagesCreator

) : ArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private val albumsMap : MutableMap<Long, Flowable<List<Album>>> = mutableMapOf()
    private val songMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private val contentProviderObserver : Flowable<List<Artist>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    arrayOf("count(*)"),
                    null, null, null,
                    false
            ).mapToOne { 0 }
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { songGateway.getAll() }
            .map { songList -> songList.asSequence()
                        .filter { it.artist != AppConstants.UNKNOWN_ARTIST }
                        .distinctBy { it.artistId }
                        .map { song ->
                            val albums = songList.asSequence()
                                    .distinctBy { it.albumId }
                                    .count { it.artistId == song.artistId }
                            val songs = songList.count { it.artistId == song.artistId }

                            song.toArtist(context, songs, albums)
                        }.sortedBy { it.name.toLowerCase() }
                        .toList()

            }.distinctUntilChanged()
            .doOnNext { imagesCreator.subscribe(createImages()) }
            .replay(1)
            .refCount()
            .doOnTerminate { imagesCreator.unsubscribe() }

    override fun createImages() : Single<Any> {
        return songGateway.getAllForImageCreation()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { it.groupBy { it.artistId } }
                .flattenAsFlowable { it.entries }
                .parallel()
                .runOn(Schedulers.io())
                .map { entry -> try {
                        runBlocking { makeImage(this@ArtistRepository.context, entry).await() }
                    } catch (ex: Exception){/*amen*/}
                }.sequential()
                .toList()
                .map { it.contains(true) }
                .onErrorReturnItem(false)
                .doOnSuccess { created ->
                    if (created) {
                        contentResolver.notifyChange(MEDIA_STORE_URI, null)
                    }
                }.map { Unit }
    }

    private fun makeImage(context: Context, map: Map.Entry<Long, List<Song>>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.ARTIST)
        FileUtils.makeImages(context, map.value, folderName, "${map.key}")
    }

    override fun getAll(): Flowable<List<Artist>> = contentProviderObserver

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(artistId: Long): Flowable<Artist> {
        return getAll().map { it.first { it.id == artistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Flowable<List<Song>> {
        var flowable = songMap[artistId]

        if (flowable == null){
            flowable = songGateway.getAll().map {
                it.asSequence().filter { it.artistId == artistId }.toList()
            }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songMap[artistId] = flowable
        }

        return flowable
    }

    override fun getAlbums(artistId: Long): Flowable<List<Album>> {
        var flowable = albumsMap[artistId]

        if (flowable == null){
            flowable = albumGateway.getAll()
                    .map { it.filter { it.artistId == artistId } }
                    .distinctUntilChanged()
                    .replay(1)
                    .refCount()

            albumsMap[artistId] = flowable
        }

        return flowable
    }

    override fun getLastPlayed(): Flowable<List<Artist>> {
        return Flowables.combineLatest(getAll(), lastPlayedDao.getAll(), { all, lastPlayed ->
            if (all.size < 10) {
                listOf()
            } else {
                lastPlayed.asSequence()
                        .map { lastPlayedArtistEntity -> all.firstOrNull { it.id == lastPlayedArtistEntity.id } }
                        .filter { it != null }
                        .map { it!! }
                        .take(10)
                        .toList()
            }
        })
    }

    override fun addLastPlayed(item: Artist): Completable = lastPlayedDao.insertOne(item)

}