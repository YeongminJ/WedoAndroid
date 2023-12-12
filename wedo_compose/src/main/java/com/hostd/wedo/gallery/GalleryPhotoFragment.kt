package com.hostd.wedo.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hostd.wedo.R
import com.hostd.wedo.databinding.LayoutGalleryPhotoBinding
import com.hostd.wedo.util.GeneralAdapter
import com.hostd.wedo.util.GeneralViewHolder
import com.hostd.wedo.util.Log
import kotlinx.coroutines.launch

class GalleryPhotoFragment: Fragment() {

    val viewModel: GalleryViewModel by activityViewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = GalleryViewModel(GalleryRepository()) as T
        }
    }

    var _binding: LayoutGalleryPhotoBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d("onCreateView")

        _binding = LayoutGalleryPhotoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    /*private var mScaleFactor = 1f
    val mScaleGestureDetector = ScaleGestureDetector(requireActivity(), object: SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))

            binding.rcView.invalidate()
            TODO 제스처에 따라 layoutmanager 를 변경하는 방법을 고민중인데, 이게 안되네
            return true
        }
    })*/

    lateinit var adapter: GeneralAdapter<String>

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GeneralAdapter(
            diffUtil = object: DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            },
            viewholderFactory = { viewType, parent ->
                GeneralViewHolder.create(parent, viewType, viewModel)
            },
            viewTypeFactory = {
//                if (it == "camera") R.layout.layout_camera_item
//                else R.layout.layout_photo_item
                R.layout.layout_photo_item
            }

        )

//        viewModel.galleries.observe(viewLifecycleOwner) {
//            Log.w("gallery : ${it.size}")
//            adapter.submitList(it.toList())
//        }
        lifecycleScope.launch {
            viewModel.galleries.collect {
                adapter.submitList(it.toMutableList())
            }
        }
        binding.rcView.adapter = adapter
        binding.rcView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    activity?.let { viewModel.fetchPhotos(it) }
                }
            }
        })
        val spanCount = (binding.rcView.layoutManager as GridLayoutManager).spanCount
        //밑에 공란이 생기는 버그 , + expand 버튼 튀어나가는 버그
//        binding.rcView.addItemDecoration(object: ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                val position = parent.getChildAdapterPosition(view)
//                if (state.itemCount -1 == position) {
//                    outRect.bottom = UiUtil.getPixel(100)
//                }
//            }
//        })

        lifecycleScope.launch {
            viewModel.selectedGalleries.collect {
                //위의 쇼트 컷 생길때 자연스럽게 생기기 위한 패딩
                if (it.isEmpty()) {
                    binding.rcView.setPadding(0,0,0,0)
                }
                else {
                    if (binding.rcView.paddingTop == 0) {
                        binding.rcView.setPadding(0, resources.getDimensionPixelOffset(R.dimen.size_gallery_shortcut), 0, 0)
                        it.firstOrNull()?.let { selItem->
                            if (selItem.url == adapter.currentList.getOrNull(0) ||
                                selItem.url == adapter.currentList.getOrNull(1) ||
                                selItem.url == adapter.currentList.getOrNull(2)) {
                                binding.rcView.smoothScrollToPosition(0)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun launchCamera() {
//        val takePictureIntent = Intent(activity, CameraPreviewActivity::class.java)
//        activityCameraResultLaunch.launch(takePictureIntent)
    }

    val activityCameraResultLaunch = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                val url = it.getStringExtra("data_url")
                url?.let { viewModel.addFromCamera(it)}
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}