package com.hostd.wedo.gallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.hostd.wedo.util.Log

class GalleryViewModel(val repository: GalleryRepository): ViewModel() {

    private val _albums: MutableStateFlow<List<AlbumItem>> = MutableStateFlow(emptyList())
    val albums: StateFlow<List<AlbumItem>> = _albums

    private val _selectedAlbums: MutableStateFlow<List<AlbumItem>> = MutableStateFlow(emptyList())
    var selectedAlbums: StateFlow<List<AlbumItem>> = _selectedAlbums

    private val _selectedGalleries: MutableStateFlow<List<SelectedPhoto>> = MutableStateFlow(emptyList())
    val selectedGalleries: StateFlow<List<SelectedPhoto>> = _selectedGalleries

    private val _galleries = MutableStateFlow<List<String>>(emptyList())
    val galleries = _galleries

    init {
//        initShortCuts()
    }
    sealed class StateEventFlow {
        class ExpandEvent(val data: String): StateEventFlow()
        class AddDoneEvent: StateEventFlow()

        class CameraEvent: StateEventFlow()
    }
    val eventFlow = MutableSharedFlow<StateEventFlow>()

    fun selectAll(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            //앨범 전에 선택된 아이템 부터 추가
            updateSelectList()
            //앨범 추가
            mBucketId?.let { repository.selectAll(context, it) }
            withContext(Dispatchers.Main) {
                eventFlow.emit(StateEventFlow.AddDoneEvent())
            }
        }
    }
    fun fetchAlbums(context: Context) {
        viewModelScope.launch {
            _albums.emit(repository.fetchAlbums(context))
        }
    }

    fun fetchAlbumFirstPhoto(context: Context, albumId: Long): String {
        return repository.fetchFirstPhoto(context, albumId)
    }

    var mBucketId: Long? = null
    var mOffset = 0
    fun fetchPhotos(context: Context, bucketId: Long? = mBucketId) {
        viewModelScope.launch {
            if (mBucketId != bucketId) {
                // 앨범이 바뀌면 오프셋 초기화
                mOffset = 0
                _galleries.emit(emptyList())
            }
            mBucketId = bucketId

            val items = repository.fetchPhotos(context, bucketId, mOffset)
            mOffset += items.size
            _galleries.value?.toMutableList()?.apply {
//                if (isEmpty()) add("camera")
                addAll(items)
                _galleries.value = (this)
            }
        }
    }

    fun addFromCamera(url: String) {
        mOffset = 0
        viewModelScope.launch {
            Log.w("newUrl : $url : ${_galleries.value.hashCode()}")
            val newList = _galleries.value.toMutableList().apply {
                add(1, url)
            }
            Log.e("newList : ${newList.hashCode()}, ${newList.size}")
            _galleries.emit(newList)
//            _galleries.emit(_galleries.value.toMutableList().apply {
//                add(0, "camera")
//            })
            _selectedGalleries.emit(_selectedGalleries.value.toMutableList().apply {
                add(SelectedPhoto(url))
            })
        }
    }

    fun updatePhotos(pagingItems: List<PagingViewModel.PagingItem>) {
        val selectedItems = _selectedGalleries.value.map { it.url }.toMutableSet()
        val newList = mutableListOf<String>()
        val removeList = mutableListOf<String>()
        pagingItems.forEach {
            if (it.isChecked == true) {
                newList.add(it.url)
            }
            else {
                if (selectedItems.contains(it.url)) {
                    removeList.add(it.url)
                }
            }
        }

        viewModelScope.launch {
            _selectedGalleries.value.filter {
                !removeList.contains(it.url)
            }.map { it.url }.toMutableSet().apply {
                addAll(newList)
                _selectedGalleries.emit(map { SelectedPhoto(it) })
            }
        }
    }

    fun onClickItem(uri: String) {
//        _selectedGalleries.value.filter { !it.isAlbum }.apply {
//            //앨범 보기에서 사진 전환시 앨범은 제거
//            _selectedGalleries.value = this
//            //추가 혹은 삭제
//            find { it.url == uri }?.let {
//                removeItem(it)
//            } ?: kotlin.run {
//                addItem(SelectedPhoto(uri))
//            }
//        }
        //앨범 보기에서 사진 전환시 앨범은 제거
        _selectedGalleries.value.apply {
            //추가 혹은 삭제
            find { it.url == uri }?.let {
                removeItem(it)
            } ?: kotlin.run {
                addItem(SelectedPhoto(uri))
            }
        }
    }

    fun onClickItem(item: AlbumItem) {
//        _selectedGalleries.value.filter { it.isAlbum }.apply {
//            // 사진 보기에서 앨범 전환시 사진 전부 제거
//            _selectedGalleries.value = this
//            //추가 혹은 삭제
//            find { it.url == item.representUrl }?.let {
//                removeItem(it)
//            } ?: kotlin.run {
//                addItem(SelectedPhoto(item.representUrl, true, item.id))
//            }
//        }
    }

    fun onClickCamera() {
        viewModelScope.launch {
            eventFlow.emit(StateEventFlow.CameraEvent())
        }
    }

    fun addItem(item: SelectedPhoto) {
        val newList = _selectedGalleries.value.toMutableList().apply {
            add(item)
        }
        viewModelScope.launch {
            _selectedGalleries.emit(newList)
        }
    }

    fun removeItem(item: SelectedPhoto) {
        val newList = _selectedGalleries.value.toMutableList().filter { it != item }
        viewModelScope.launch {
            _selectedGalleries.emit(newList)
        }
    }

    fun clickExpand(item: String) {
        viewModelScope.launch {
            eventFlow.emit(StateEventFlow.ExpandEvent(item))
        }
    }

    fun clickExpand(item: SelectedPhoto) {
        viewModelScope.launch {
            eventFlow.emit(StateEventFlow.ExpandEvent(item.url))
        }
    }

    fun updateSelectList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSavedGalleries(_selectedGalleries.value.map { GalleryData(it.url) })
        }
    }
}