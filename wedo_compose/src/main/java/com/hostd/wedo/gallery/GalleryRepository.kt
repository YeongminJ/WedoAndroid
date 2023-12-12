package com.hostd.wedo.gallery

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.hostd.wedo.R
import com.hostd.wedo.util.Log

class GalleryRepository {

    suspend fun updateSavedGalleries(newList: List<GalleryData>) {
        newList.forEach {
            GalleryDatabase.getInstance().galleryDao().insert(it)
        }
    }

    suspend fun fetchSavedGalleries(pageCount: Int = 0): List<GalleryData> {
        return GalleryDatabase.getInstance().galleryDao().getAll(pageCount)
    }

    suspend fun fetchSelectedPhotos(): List<SelectedPhoto> {
        val sampleList = GalleryDatabase.getInstance().galleryDao().getSample()
        val size = GalleryDatabase.getInstance().galleryDao().getSize()
        val visibleSize = 3
        return sampleList.mapIndexed { index, galleryData ->
            if (index == visibleSize-1) {
                SelectedPhoto(galleryData.url, "+${size-visibleSize}")
            }
            else {
                SelectedPhoto(galleryData.url)
            }
        }
    }

    fun fetchAlbums(context: Context): List<AlbumItem> {
        val bucketList = mutableListOf<AlbumItem>()
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.BUCKET_ID),
            null,
            null,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC"
        )
        val startTime = System.currentTimeMillis()
        bucketList.add(AlbumItem(0, context.getString(R.string.total)))
        cursor?.use {
            val bucketSet = mutableSetOf<Long>()
            var totalCount = 0
            while (cursor.moveToNext()) {
                try {
                    val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
                    val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    if (bucketName.isNullOrEmpty()) continue
                    if (bucketSet.contains(albumId)) continue
                    else {
                        bucketSet.add(albumId)
                        var size: Int
                        val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val bundle = bundleOf(
                                ContentResolver.QUERY_ARG_SQL_SELECTION to "${MediaStore.Images.Media.BUCKET_ID}=${albumId}"
                            )
                            context.contentResolver.query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                arrayOf(MediaStore.Images.Media._ID),
                                bundle,
                                null
                            )
                        } else {
                            context.contentResolver.query(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                arrayOf(MediaStore.Images.Media._ID),
                                "${MediaStore.Images.Media.BUCKET_ID}=${albumId}",
                                null,
                                null
                            )
                        }
                        size = query?.count ?: 0
                        totalCount += size
                        query?.close()
                        Log.e("bucketId : $albumId, bucketName : ${bucketName}, size : $size")
                        val firstPhotoUrl = fetchFirstPhoto(context, albumId)
                        bucketList.add(AlbumItem(albumId, bucketName, size, firstPhotoUrl))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            it.close()
            bucketList[0].size = totalCount
        }
        Log.e("앨범숫자 : ${bucketList.size}, CalcTime : ${(System.currentTimeMillis()-startTime)/1000}")
        return bucketList
    }

    fun fetchFirstPhoto(context: Context, bucketId: Long): String {
        context.apply {
            val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bundle = bundleOf(
                    ContentResolver.QUERY_ARG_LIMIT to 1,
                    ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED),
                    ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                )
                bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.Media.BUCKET_ID}=${bucketId}")
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    bundle,
                    null
                )
            } else {
                val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 1"
                val SELECTION = bucketId.let {
                    MediaStore.Images.Media.BUCKET_ID + "=${bucketId}"
                }

                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    SELECTION,
                    null,
                    sortOrder
                )
            }
            var result = ""
            query?.use { cursor ->
                while (cursor.moveToNext()) {
                    cursor.columnNames.forEach {
                        val index = cursor.getColumnIndex(it)
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(index))
                        result = uri.toString()
                    }
                }
                cursor.close()
            }

            return result
        }
    }

    private var limitCount = 100
    fun fetchPhotos(context: Context, bucketId: Long? = null, mOffset: Int = 0): List<String> {
        context.apply {
            val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bundle = bundleOf(
                    ContentResolver.QUERY_ARG_OFFSET to mOffset,
                    ContentResolver.QUERY_ARG_LIMIT to limitCount,
                    ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED),
                    ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                )
                if (bucketId != null) {
                    bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.Media.BUCKET_ID}=${bucketId}")
                }
                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    bundle,
                    null
                )
            } else {
                val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 30 OFFSET $mOffset"
                val SELECTION = bucketId?.let {
                    MediaStore.Images.Media.BUCKET_ID + "=${bucketId}"
                } ?: kotlin.run { null }

                contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    SELECTION,
                    null,
                    sortOrder
                )
            }
            val items = mutableListOf<String>()
            query?.use { cursor ->
                //카메라 안그림
    //                if (adapter.itemList.isEmpty()) {
    //                    items.add(Uri.parse(""))    //카메라용
    //                }
                while (cursor.moveToNext()) {
                    cursor.columnNames.forEach {
                        val index = cursor.getColumnIndex(it)
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(index))
                        items.add(uri.toString())
                    }
                }
                cursor.close()
            }

            Log.e("WTF_JDI", "items : ${items.size}")
            return items
        }
    }

    fun removeGallery(data: String) {
        GalleryDatabase.getInstance().galleryDao().delete(data)
    }

    suspend fun removeGallery(data: GalleryData) {
        GalleryDatabase.getInstance().galleryDao().delete(data)
    }

    suspend fun deleteAll() {
        GalleryDatabase.getInstance().galleryDao().deleteAll()
    }

    suspend fun selectAll(context: Context, bucketId: Long) {
        Log.e("WTF_JDI","Start Insert All")
        context.apply {
            var mOffset = 0
            val limitCount = 1000
            var isRunning = true

            while (isRunning) {
                val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val bundle = bundleOf(
                        ContentResolver.QUERY_ARG_OFFSET to mOffset,
                        ContentResolver.QUERY_ARG_LIMIT to limitCount,
                        ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED),
                        ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                    )
                    if (bucketId != null) {
                        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.Media.BUCKET_ID}=${bucketId}")
                    }
                    contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.Media._ID),
                        bundle,
                        null
                    )
                } else {
                    val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 30 OFFSET $mOffset"
                    val SELECTION = bucketId?.let {
                        MediaStore.Images.Media.BUCKET_ID + "=${bucketId}"
                    } ?: kotlin.run { null }

                    contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.Media._ID),
                        SELECTION,
                        null,
                        sortOrder
                    )
                }
                val items = mutableListOf<String>()
                query?.use { cursor ->
                    while (cursor.moveToNext()) {
                        cursor.columnNames.forEach {
                            val index = cursor.getColumnIndex(it)
                            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(index))
                            items.add(uri.toString())
                        }
                    }
                    cursor.close()
                }
                GalleryDatabase.getInstance().galleryDao().insert(items.map { GalleryData(it) })
                mOffset += limitCount
                if (items.size < limitCount) {
                    isRunning = false
                }
                Log.e("WTF_JDI", "Insert Loop : ${items.size}")
            }
        }

    }
}

data class AlbumItem(
    var id: Long = 0,
    var name: String = "",
    var size: Int = 0,
    var representUrl: String = ""
) {
    override fun toString(): String {
        return "$name ($size)"
    }
}

data class SelectedPhoto(
    var url: String = "",
    val lastText: String = ""
)