package dev.olog.presentation.base

import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.presentation.base.anim.ScaleInOnTouch
import dev.olog.presentation.base.anim.ScaleMoreInOnTouch
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.model.BaseModel

fun <T : BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T : BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    @IdRes resId: Int,
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T : BaseModel> RecyclerView.ViewHolder.setOnLongClickListener(
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    itemView.setOnLongClickListener inner@{
        if (adapterPosition != RecyclerView.NO_POSITION) {
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
                ?: return@inner false
            return@inner true
        }
        false
    }
}

fun RecyclerView.ViewHolder.elevateAlbumOnTouch() {
    itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
}

fun RecyclerView.ViewHolder.elevateSongOnTouch() {
    val viewToAnimate = itemView.findViewById<View>(R.id.root)?.let { it } ?: itemView
    itemView.setOnTouchListener(ScaleInOnTouch(viewToAnimate))
}

fun RecyclerView.ViewHolder.setOnDragListener(dragHandleId: Int, dragListener: IDragListener) {
    itemView.findViewById<View>(dragHandleId)?.setOnTouchListener { view, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
//                (view.parent.parent).requestDisallowInterceptTouchEvent(false)
                dragListener.onStartDrag(this)
                true
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
//                (view.parent.parent).requestDisallowInterceptTouchEvent(true)
                false
            }
            else -> false
        }
    }
}