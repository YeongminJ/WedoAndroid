package com.hostd.wedo.gallery

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hostd.wedo.util.Log

object GalleryBindingAdapter {

    @JvmStatic
    @BindingAdapter("bindImageUrl")
    fun bindImageUrl(view: ImageView, url: String) {
        Glide.with(view.context).load(url)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("bindImageNoRoundUrl")
    fun bindImageNoRoundUrl(view: ImageView, url: String) {
        Glide.with(view.context).load(url)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("bindAlbum")
    fun bindAlbum(view: ImageView, data: AlbumItem) {
        view.context.apply {
            Glide.with(view.context).load(data.representUrl).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bindImageUrl", "bindViewModel")
    fun bindImageSelection(view: ImageView, url: String, viewModel: GalleryViewModel) {
        view.isSelected = viewModel.selectedGalleries.value.find { it.url == url} != null
    }

    @JvmStatic
    @BindingAdapter("bindSelection", "bindViewModel")
    fun bindImageSelection(view: View, url: String, viewModel: GalleryViewModel) {
        view.isSelected = viewModel.selectedGalleries.value.find { it.url == url} != null
    }

    @JvmStatic
    @BindingAdapter("bindAlbum", "bindViewModel")
    fun bindImageSelection(view: ImageView, data: AlbumItem, viewModel: GalleryViewModel) {
        viewModel.selectedGalleries.value.find { it.url == data.representUrl }?.let { View.VISIBLE } ?: kotlin.run { View.GONE }
    }


    @JvmStatic
    @BindingAdapter("bindImageRadio")
    fun bindRadioCheck(view: ImageView, isChecked: Boolean? = null) {
        if (isChecked == null) view.visibility = View.GONE
        else view.visibility = View.VISIBLE
        Log.d("bindRadio : $isChecked")
        view.isSelected = isChecked == true
    }

    @JvmStatic
    @BindingAdapter("bindTextByVM")
    fun bindTextByVM(view: TextView, viewModel: PagingViewModel) {
        Log.d("bindTextByVM : ${viewModel.selectedCount}")
        view.text = viewModel.selectedCount.toString()
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        cameraProvider.unbindAll()

        Log.e("bindCameraPreview 333")

        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        previewView.findViewTreeLifecycleOwner()?.let {
            Log.e("bindCameraPreview 444")
            cameraProvider.bindToLifecycle(it, cameraSelector, preview)
            previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

}