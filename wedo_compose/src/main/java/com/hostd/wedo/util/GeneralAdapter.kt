package com.hostd.wedo.util

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class GeneralAdapter<DATA>(
    diffUtil: DiffUtil.ItemCallback<DATA>,
    val viewholderFactory: (viewType: Int, parent: ViewGroup) -> GeneralViewHolder<DATA>,
    val viewTypeFactory: ((DATA) -> Int)?
): ListAdapter<DATA, GeneralViewHolder<DATA>>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder<DATA> {
        return viewholderFactory.invoke(viewType, parent)
    }

    override fun onBindViewHolder(holder: GeneralViewHolder<DATA>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeFactory?.invoke(getItem(position)) ?: super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return currentList[position]?.hashCode()?.toLong() ?: 0L
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}