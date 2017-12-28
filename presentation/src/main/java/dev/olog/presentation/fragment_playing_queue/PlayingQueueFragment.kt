package dev.olog.presentation.fragment_playing_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.animation.CircularReveal
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
    }

    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = CircularReveal(activity!!.playingQueue)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper
    }

    override fun onResume() {
        super.onResume()
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        view!!.back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue

}