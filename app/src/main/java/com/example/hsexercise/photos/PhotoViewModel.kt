package com.example.hsexercise.photos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hsexercise.common.database.PhotoDatabase
import com.example.hsexercise.photos.model.Photo
import com.example.hsexercise.photos.network.PhotoService
import com.example.hsexercise.photos.repository.PhotoRepository
import com.example.hsexercise.photos.state.StateData
import com.example.hsexercise.photos.state.StateData.State.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val compositeDisposable = CompositeDisposable()
    lateinit var photoService: PhotoService

    private val repository: PhotoRepository
    val stateData = MutableLiveData<StateData<List<Photo>>>()

    init {
        val photoDao = PhotoDatabase.getDatabase(application).photoDao()
        repository = PhotoRepository(photoDao)

        stateData.postValue(StateData(state = LOADING))
    }

    fun load() {
        compositeDisposable.add(photoService.listRepos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                { photos ->
                    stateData.postValue(StateData(photos, SUCCESS))
                    viewModelScope.launch { repository.insertAll(photos) }
                },
                { error ->
                    stateData.postValue(StateData(state = ERROR, error = error))
                }
            )
        )
    }

    fun clear() {
        compositeDisposable.clear()
    }
}
