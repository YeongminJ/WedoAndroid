package com.hostd.wedo.gallery

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.hostd.wedo.databinding.DialogTwoButtonBinding

class DialogTwoButton: DialogFragment() {

    companion object {
        fun newInstance(title: String? = null, desc: String, okText: String? = null, cancelText: String? = null, extraDesc: String? = null, imgRes: Int = 0): DialogTwoButton {
            val args = Bundle()
            args.putString("title", title)
            args.putString("desc", desc)
            args.putString("okStr", okText)
            args.putString("cancelStr", cancelText)
            args.putString("extraDesc", extraDesc)
            args.putInt("imgRes", imgRes)

            val fragment = DialogTwoButton()
            fragment.arguments = args
            return fragment
        }
    }

    var mSpanable: SpannableStringBuilder? = null
    private var mOkListener: (() -> Unit)? = null
    private var mCancelListener: (()-> Unit)? = null
    lateinit var binding: DialogTwoButtonBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogTwoButtonBinding.inflate(inflater, container, false)

//        ApplicationUtils.fixLocale(context, AppManager.getConstants().fixLocale)

        val param = WindowManager.LayoutParams()
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        param.width
        param.dimAmount = 0.8f
        dialog?.window?.let {
            it.attributes = param
        }
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        arguments?.apply {
            getString("title")?.let {
                if (it.isEmpty()) binding.textTitle.visibility = View.GONE
                else binding.textTitle.visibility = View.VISIBLE
                binding.textTitle.text = it
            } ?: kotlin.run {
                binding.textTitle.visibility = View.GONE
            }

            binding.textDes.text = getString("desc")
            getString("okStr")?.let {
                binding.btnDone.text = it
            }
            getString("cancelStr")?.let {
                binding.btnCancel.text = it
            }
            getString("extraDesc")?.let {
                binding.textExtra.visibility = View.VISIBLE
                binding.textExtra.text = it
            }
            getInt("imgRes").let {
                if (it != 0) {
                    binding.imgTitle.visibility = View.VISIBLE
                    binding.imgTitle.setImageResource(it)
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            mCancelListener?.invoke()
            dismiss()
        }
        binding.btnDone.setOnClickListener {
            mOkListener?.invoke()
            dismiss()
        }
        if (mSpanable?.isNotEmpty() == true) {
            binding.textExtra.visibility = View.VISIBLE
            binding.textExtra.text = mSpanable
        }
    }

    fun addOkListener(okListener: ()-> Unit) {
        mOkListener = okListener
    }

    fun addCancelListener(cancelListener: () ->Unit) {
        mCancelListener = cancelListener
    }

    fun addExtraSpan(spannableBuilder: SpannableStringBuilder) {
        mSpanable = spannableBuilder
    }
}