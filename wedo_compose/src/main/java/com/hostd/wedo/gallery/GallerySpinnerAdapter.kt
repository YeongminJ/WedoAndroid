package com.hostd.wedo.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.hostd.wedo.R
import com.hostd.wedo.databinding.ItemBgSpinnerBinding
import com.hostd.wedo.databinding.ItemSpinnerInnerBinding

class SpinnerAdapter (context: Context,
                      @LayoutRes private val resId: Int,
                      private val values: List<AlbumItem>,
                      var selectedPosition: Int
) : ArrayAdapter<AlbumItem>(context, resId, values) {

    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_MY_LIST = 1
    }

    override fun getCount() = values.size


    override fun getItem(position: Int) = values[position]
    

    @SuppressLint("ViewHolder", "StringFormatInvalid")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemBgSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        try {
            values.getOrNull(position)?.let { model->
                binding.textName.text = "${model.name} (${model.size})"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerInnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        try {
            values.getOrNull(position)?.let { model->
                binding.textExpandItem.text = "${model.name} (${model.size})"
                if(position == selectedPosition) {
                    binding.root.isSelected = true
                    binding.textExpandItem.setTextColor(context.getColor(R.color.selector_color_spinner_text))
                    binding.textExpandItem.setTypeface(null, Typeface.BOLD)
                } else {
                    binding.root.isSelected = false
                    binding.textExpandItem.setTypeface(null, Typeface.NORMAL)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

}

data class SpinnerModel(
    val name: String,
    val size: Int
)