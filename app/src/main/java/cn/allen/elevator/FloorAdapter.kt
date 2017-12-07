package cn.allen.elevator

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * Author: AllenWen
 * CreateTime: 2017/12/7
 * Email: wenxueguo@medlinker.com
 * Description:
 */
class FloorAdapter(private var mList: MutableList<Int>) : RecyclerView.Adapter<FloorAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_floor, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Int = mList[position]
        holder.index.text = data.toString()
    }

    inner class ViewHolder(item: View?) : RecyclerView.ViewHolder(item) {
        var index: TextView = itemView.find(R.id.index)
    }
}