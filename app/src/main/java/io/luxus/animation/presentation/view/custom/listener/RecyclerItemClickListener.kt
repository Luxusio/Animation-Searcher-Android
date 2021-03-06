package io.luxus.animation.presentation.view.custom.listener

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// https://www.programmersought.com/article/7738162636/
class RecyclerItemClickListener constructor(
    context: Context,
    mListener: OnItemClickListener
): RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        /**
         * Click
         */
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (selectView != null) {
                mListener.onItemClick(selectView!!, selectPosition)
                return true
            }
            return super.onSingleTapUp(e)
        }

        /**
         * Press
         */
        override fun onLongPress(e: MotionEvent) {
            if (selectView != null) {
                mListener.onItemLongClick(selectView!!, selectPosition)
            }
        }
    })

    /**
     * Selected view
     */
    private var selectView: View? = null

    /**
     * The position of the selected view
     */
    private var selectPosition = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y) ?: return false
        selectView = childView
        selectPosition = rv.getChildAdapterPosition(childView)
        /**
         * Handed over to the gesture control class to handle
         */
        return mGestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
        open class Builder: OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {}
            override fun onItemLongClick(view: View, position: Int) {}
        }
    }


}