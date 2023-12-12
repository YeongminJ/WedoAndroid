package com.hostd.wedo.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.hostd.wedo.BR

open class GeneralViewHolder<DATA>(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun <DATA> create(parent: ViewGroup, @LayoutRes layout: Int, viewModel: ViewModel): GeneralViewHolder<DATA> {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layout, parent, false).apply {
                //Note BR 은 Databinding 에 의해 빌드 시 자동 생성,
                // Libcore 에서 만들어진 GeneralHolder 를 모든 모듈에 사용하기 위해 Libcore/item_for_br.xml 내에 vm (ViewModel), data ( DataClass ) 를 작성
                // 추가적인 모듈이 피룡할 때, item_for_br 에도 variable 을 추가해야함!
                setVariable(BR.vm, viewModel)
            }
            return GeneralViewHolder(binding)
        }
    }

    open fun bind(item: DATA) {
        binding.apply {
            setVariable(BR.data, item)
            Log.e("Bind : item $item")
        }.run {
            executePendingBindings()
        }
    }
}