package com.hostd.wedo.gallery

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.hostd.wedo.R
import com.hostd.wedo.databinding.ActivityGalleryPickerBinding
import com.hostd.wedo.util.GeneralAdapter
import com.hostd.wedo.util.GeneralViewHolder
import com.hostd.wedo.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryPickerActivity : AppCompatActivity() {

    lateinit var binding: ActivityGalleryPickerBinding
    val viewModel: GalleryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = GalleryViewModel(GalleryRepository()) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryPickerBinding.inflate(LayoutInflater.from(this), null, false)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentResolver?.refresh(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null)
        }

        observeAlbums()
        observeSelectedGallery()
        observeListeners()

        checkPermission(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.READ_MEDIA_IMAGES,
            getString(R.string.txt_permission_require_photo_title),
            getString(R.string.txt_permission_require_photo_desc),
            getString(R.string.confirm)
        )
    }

    fun checkPermission(permission: String, denyMessage: String, rationalExtra: String, okText: String) {
        TedPermission.create().setPermissions(permission)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    if (permission == Manifest.permission.CAMERA) {
                        if (supportFragmentManager.findFragmentById(binding.fieldFragment.id) is GalleryPhotoFragment) {
                            (supportFragmentManager.findFragmentById(binding.fieldFragment.id) as GalleryPhotoFragment).launchCamera()
                        }
                    }
                    else {
                        initFragment()
                        viewModel.fetchAlbums(this@GalleryPickerActivity)
                    }
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    val dialog = DialogTwoButton.newInstance(
                        desc = denyMessage,
                        extraDesc = rationalExtra,
                        okText = okText
                    )
                    dialog.addOkListener {
                        try {
                            val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:$packageName"))
                            permissionResultLauncher.launch(intent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            startActivity(intent)
                        }
                    }
                    dialog.addCancelListener {
                        finish()
                    }
                    dialog.show(supportFragmentManager, null)
                }

            })
            .check()
    }
    override fun onResume() {
        super.onResume()
    }

    fun observeListeners() {
        binding.btnClose.setOnClickListener { finish() }
        binding.btnComplete.setOnClickListener {
            viewModel.updateSelectList()
            finish()
        }
        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is GalleryViewModel.StateEventFlow.ExpandEvent -> {

                        Intent(this@GalleryPickerActivity, PagingGalleryActivity::class.java).apply {
                            val selList = viewModel.selectedGalleries.value
                            val index = viewModel.galleries.value.indexOf(event.data)

                            putExtra("start_index", index)
                            putExtra("list",
                                ArrayList(viewModel.galleries.value.map {
                                        PagingViewModel.PagingItem(it, (selList.find { item-> item.url == it } != null))
                                    }
                                )
                            )
                            galleryPagingLauncher.launch(this)
                        }
                    }
                    is GalleryViewModel.StateEventFlow.AddDoneEvent -> {
                        //finish
                        finish()
                    }
                    is GalleryViewModel.StateEventFlow.CameraEvent -> {
//                        if (this@GalleryPickerActivity.hasCameraPermission) {
//                            //카메라찍기로 이동
//                            if (supportFragmentManager.findFragmentById(binding.fieldFragment.id) is GalleryPhotoFragment) {
//                                (supportFragmentManager.findFragmentById(binding.fieldFragment.id) as GalleryPhotoFragment).launchCamera()
//                            }
//                        }
//                        else {
//                            //권한 요청
//                            checkPermission(
//                                Manifest.permission.CAMERA,
//                                getString(R.string.txt_permission_require_camera_title),
//                                getString(R.string.txt_permission_require_camera_desc),
//                                getString(R.string.txt_do_permission)
//                            )
//
//                        }
                    }
                }
            }
        }
    }

    private fun observeAlbums() {
        binding.spinnerAlbum.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (binding.spinnerAlbum.adapter as SpinnerAdapter).selectedPosition = position
                val item = binding.spinnerAlbum.getItemAtPosition(position) as AlbumItem
                val albumId = if (item.id == 0L) null else item.id
                viewModel.fetchPhotos(this@GalleryPickerActivity, albumId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        lifecycleScope.launch {
            viewModel.albums.collect {
                lifecycleScope.launch {
                    binding.spinnerAlbum.adapter = SpinnerAdapter(this@GalleryPickerActivity, R.layout.item_bg_spinner, it, 0)
                }
            }
        }
    }

    private fun observeSelectedGallery() {
        val adapter = GeneralAdapter(diffUtil = object : ItemCallback<SelectedPhoto>() {
            override fun areItemsTheSame(oldItem: SelectedPhoto, newItem: SelectedPhoto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SelectedPhoto, newItem: SelectedPhoto): Boolean {
                return oldItem.toString() == newItem.toString()
            }

        }, viewholderFactory = { viewType, viewGroup ->
            GeneralViewHolder.create(viewGroup, viewType, viewModel)
        }, viewTypeFactory = {
            R.layout.layout_saved_gallery_item
        }).apply {
//            binding.recyclerShortcuts.addItemDecoration(object : ItemDecoration() {
//                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                    outRect.right = 30
//                }
//            })
            binding.recyclerShortcuts.adapter = this
        }
        lifecycleScope.launch {
            viewModel.selectedGalleries.collect {
                val originSize = adapter.currentList.size
                adapter.submitList(it.toMutableList())

                lifecycleScope.launch {
                    if (originSize != 0 && originSize < it.size) {
                        if (adapter.currentList.size - 1 >= 0) {
                            withContext(Dispatchers.IO) { delay(200) }
                            binding.recyclerShortcuts.smoothScrollToPosition(adapter.currentList.size - 1)
                        }
                    }
                    if (it.isNotEmpty()) {
                        if (binding.fieldShortcuts.visibility == View.GONE) {
                            //anim
                            binding.fieldShortcuts.visibility = View.VISIBLE
                        }
                        binding.textSelCount.visibility = View.VISIBLE
                        binding.btnComplete.visibility = View.VISIBLE
                        binding.textSelCount.text = it.size.toString()
                    }
                    else {
                        binding.fieldShortcuts.visibility = View.GONE
                        binding.textSelCount.visibility = View.GONE
                        binding.btnComplete.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun initFragment() {
        Log.d("initFragment")
        val photoFragment = GalleryPhotoFragment()
//        val albumFragment = GalleryAlbumFragment()

        binding.spinnerAlbum.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(binding.fieldFragment.id, photoFragment).commitAllowingStateLoss()
    }

    val galleryPagingLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            try {
                val list = it.data?.getSerializableExtra("result_list") as List<PagingViewModel.PagingItem>
                if (list.isNotEmpty()) viewModel.updatePhotos(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val permissionResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        checkPermission(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.READ_MEDIA_IMAGES,
            getString(R.string.txt_permission_require_photo_title),
            getString(R.string.txt_permission_require_photo_desc),
            getString(R.string.confirm)
        )
    }
}