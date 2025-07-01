package com.paintcolor.drawing.paint.base

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseSyncDifferAdapter<M : Any, VH : BaseViewHolder<M, *>> : RecyclerView.Adapter<VH>() {
    private var listData: List<M> = emptyList()
    val currentList: List<M> get() = listData

    protected abstract fun createViewHolder(viewType: Int, parent: ViewGroup): VH

    protected abstract fun layoutResource(position: Int): Int

    protected abstract fun areItemsTheSame(oldItem: M, newItem: M): Boolean

    protected abstract fun areContentsTheSame(oldItem: M, newItem: M): Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        createViewHolder(viewType, parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(listData[position])
    }

    override fun getItemViewType(position: Int): Int = layoutResource(position)

    override fun getItemCount(): Int = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list: List<M>) {
        listData = list
        notifyDataSetChanged()
    }

    fun submitList(newList: List<M>) {
        if (newList == listData) return

        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = listData.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(listData[oldItemPosition], newList[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areContentsTheSame(listData[oldItemPosition], newList[newItemPosition])
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listData = newList
        diffResult.dispatchUpdatesTo(this)
    }

}

