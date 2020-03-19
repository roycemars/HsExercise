package com.example.hsexercise.photos.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.hsexercise.photos.database.PhotoDao
import com.example.hsexercise.photos.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val photoDao: PhotoDao) {
    val photos: LiveData<List<Photo>> = photoDao.getAllPhotos()

    suspend fun insert(photo: Photo) {
        photoDao.insert(photo)
    }

    suspend fun insertAll(photos: List<Photo>) {
        withContext(Dispatchers.IO) {
            photoDao.insertAll(photos)
        }
    }
}