package com.qxy.douyinDemo.ui.listAdapter

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.qxy.douyinDemo.mvvm.viewModel.MovieRankViewModel

class MovieItemDecoration(
    private val mActivity: Activity,
    private val mOrientation: Int,
    private val mLayout: Int,
    private val movieRankViewModel: MovieRankViewModel) : ItemDecoration() {
    private var header: View? = null

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.childCount > 0) {
            val child = parent.getChildAt(0)
            if (parent.getChildAdapterPosition(child) != 0) {
                return
            }
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                canvas.translate(0f, (child.top - header!!.height).toFloat())
            } else {
                canvas.translate((child.left - header!!.width).toFloat(), 0f)
            }
        } else {
            if (header == null) {
                initHeader(parent)
            }
        }
        header!!.draw(canvas)
        header?.let{ it ->
            val group = it as ViewGroup
            val group2 = group[0] as ViewGroup
            val view = group2[0] as TextView
            movieRankViewModel.movieRank.observe(mActivity as ComponentActivity){
                view.text = it.active_time
                view.invalidate()
            }
        }
    }

    private fun initHeader(parent: RecyclerView) {
        if (header == null) {
            header = LayoutInflater.from(mActivity).inflate(mLayout, parent, false)
            if (header?.layoutParams == null) {
                header?.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val widthSpec: Int
            val heightSpec: Int
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
                heightSpec =
                    View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
            } else {
                widthSpec =
                    View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.UNSPECIFIED)
                heightSpec =
                    View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.EXACTLY)
            }
            val childWidth = header?.layoutParams?.let {
                ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    parent.paddingLeft + parent.paddingRight, it.width
                )
            }
            val childHeight = header?.layoutParams?.let {
                ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    parent.paddingTop + parent.paddingBottom, it.height
                )
            }
            if (childWidth != null) {
                if (childHeight != null) {
                    header?.measure(childWidth, childHeight)
                }
            }
            header?.measuredWidth?.let { header?.layout(0, 0, it, header!!.measuredHeight) }
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State) {
        if (header == null) {
            initHeader(parent)
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect[0, header!!.measuredHeight, 0] = 0
            } else {
                outRect[header!!.measuredWidth, 0, 0] = 0
            }
        }
    }
}