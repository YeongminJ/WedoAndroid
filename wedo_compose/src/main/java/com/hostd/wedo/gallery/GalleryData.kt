package com.hostd.wedo.gallery

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(indices = [Index(value = ["url"], unique = true)])
data class GalleryData(
    var url: String = "",
    var insertTime: Long = System.currentTimeMillis()
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
/*data class GalleryData(
    //앨범 선택시
    var bucketList: List<Long> = emptyList(),
    //사진 선택시
    var photoList: List<String> = emptyList(),
)*/

@Dao
interface GalleryDao {
    //중복되는것 제거하기 위함. REPLACE 가 되면 선택시간이 최근으로 변경되서 제외
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(gallery: GalleryData): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(gallery: List<GalleryData>)

    @Update
    fun update(gallery: GalleryData)

    @Delete
    fun delete(gallery: GalleryData)

    @Delete
    fun delete(galleries: List<GalleryData>)

    @Query("DELETE FROM GalleryData")
    fun deleteAll()

    @Query("DELETE FROM GalleryData WHERE url = :url")
    fun delete(url: String)

    @Query("SELECT * FROM GalleryData ORDER BY insertTime DESC LIMIT 100 OFFSET :pageCount*100")
    fun getAll(pageCount: Int): List<GalleryData>

    @Query("SELECT * FROM GalleryData ORDER BY insertTime DESC LIMIT 4")
    fun getSample(): List<GalleryData>

    @Query("SELECT COUNT(id) FROM GalleryData")
    fun getSize(): Int

    @Query("SELECT * FROM GALLERYDATA ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): GalleryData
}

data class GalleryItem(
    var url: String,
    var isEdit: Boolean = false
)
