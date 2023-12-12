package com.hostd.wedo.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.hostd.wedo.R
import com.hostd.wedo.databinding.LayoutGalleryPhotoBinding
import com.hostd.wedo.util.Log
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.hostd.wedo.util.GeneralAdapter
import com.hostd.wedo.util.GeneralViewHolder

class GalleryAlbumFragment: Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GeneralAdapter(
            diffUtil = object: DiffUtil.ItemCallback<AlbumItem>() {
                override fun areItemsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
                    return oldItem.toString() == newItem.toString()
                }
            },
            viewholderFactory = { viewType, parent ->
                GeneralViewHolder.create(parent, viewType, viewModel)
            },
            viewTypeFactory = { R.layout.layout_photo_item }
        )

        lifecycleScope.launch {
            viewModel.albums.collectLatest {
                //전체 제외
                adapter.submitList(it.filterIndexed { index, albumItem -> albumItem.id.toInt() != 0 })
            }
        }
        binding.rcView.adapter = adapter
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
                            if (selItem.url == adapter.currentList.getOrNull(0)?.representUrl ||
                                selItem.url == adapter.currentList.getOrNull(1)?.representUrl ||
                                selItem.url == adapter.currentList.getOrNull(2)?.representUrl) {
                                binding.rcView.smoothScrollToPosition(0)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}