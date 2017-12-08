package cn.allen.elevator

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Author: AllenWen
 * CreateTime: 2017/12/8
 * Email: wenxueguo@medlinker.com
 * Description:
 */
class RecyclerViewCompat(context: Context) : RecyclerView(context) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, expandSpec)
    }
}