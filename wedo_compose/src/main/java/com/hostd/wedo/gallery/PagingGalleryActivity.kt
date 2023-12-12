package com.hostd.wedo.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import com.hostd.wedo.R
import com.hostd.wedo.databinding.ActivityPagingGalleryBinding
import com.hostd.wedo.util.GeneralAdapter
import com.hostd.wedo.util.GeneralViewHolder
import com.hostd.wedo.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable

class PagingGalleryActivity : AppCompatActivity() {

    lateinit var binding: ActivityPagingGalleryBinding
    val viewModel: PagingViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PagingViewModel() as T
            }
        }
    }
    var adapter: GeneralAdapter<PagingViewModel.PagingItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startIndex = intent.getIntExtra("start_index", 0)
        val itemList = intent.getSerializableExtra("list") as ArrayList<PagingViewModel.PagingItem>

        viewModel.initItems(itemList)
        adapter = GeneralAdapter(
            viewTypeFactory = {
                R.layout.layout_paging_item
            },
            viewholderFactory = { viewType, parent ->
                GeneralViewHolder.create(parent, viewType, viewModel)
            },
            diffUtil = object : ItemCallback<PagingViewModel.PagingItem>() {
                override fun areItemsTheSame(oldItem: PagingViewModel.PagingItem, newItem: PagingViewModel.PagingItem): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: PagingViewModel.PagingItem, newItem: PagingViewModel.PagingItem): Boolean {
                    return false
                }
            }
        ).apply {
            binding.pager.adapter = this
            submitList(itemList) {
                binding.pager.post {
                    binding.pager.setCurrentItem(startIndex, false)
                }
            }
        }
        //깜빡임 방지
        binding.pager.setPageTransformer(DepthPageTransformer())
        viewModel.galleryItems.observe(this) {
            Log.d("gallery Collected")
//            val current = binding.pager.currentItem
            adapter?.submitList(it.toMutableList()) {
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collectLatest {
                    when (it) {
                        "back"-> {
                            onBackPressed()
                        }
                        "click" -> {
                            binding.pager.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        Intent().apply {
            putExtra("result_list", adapter?.currentList?.let { ArrayList(it) })
            setResult(Activity.RESULT_OK, this)
        }

        finish()
    }
}

class PagingViewModel: ViewModel() {
    val stateFlow: MutableSharedFlow<String> = MutableSharedFlow()

    val galleryItems: MutableLiveData<List<PagingItem>> = MutableLiveData(emptyList())

    var selectedCount = 0
    fun initItems(items: List<PagingItem>) {

        items.forEach {
            if (it.isChecked == true) selectedCount++
        }
        val newList = items.map {
            it.selectCount = selectedCount
            it
        }
        viewModelScope.launch {
            galleryItems.postValue(newList)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            stateFlow.emit("back")
        }
    }

    fun onRadioClick(data: PagingItem) {
        if (data.isChecked == null) return
        data.isChecked = !data.isChecked!!
        val newCount = if (data.isChecked == true) {
            data.selectCount + 1
        } else {
            data.selectCount - 1
        }
        galleryItems.value?.let {
            val newList = it.map {
                it.selectCount = newCount
                it
            }
            galleryItems.postValue(newList)
        }


        viewModelScope.launch {

//            galleryItems.postValue(galleryItems.value.map {
//                //emit 은 리스트 개수 동일하고 특정 요소 변경시에 동일하다고 판단하여 전송안됨.
//                data.copy(url = it.url, isChecked = it.isChecked, selectCount = newCount)
//            })
//            stateFlow.emit("click")
        }
    }
    data class PagingItem(
        var url: String,
        var isChecked: Boolean? = null,
        var selectCount: Int = 0
    ): Serializable
}

class DepthPageTransformer : PageTransformer {
    private val MIN_SCALE = 0.75f
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page.
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition.
                    translationX = pageWidth * -position
                    // Move it behind the left page.
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1).
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}
