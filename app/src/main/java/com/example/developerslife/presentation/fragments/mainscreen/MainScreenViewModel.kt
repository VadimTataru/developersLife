package com.example.developerslife.presentation.fragments.mainscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.developerslife.data.models.MemeModel
import com.example.developerslife.domain.usecase.GetMemeUseCase
import com.example.developerslife.utils.Constants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class MainScreenViewModel(
    private val getMemeUseCase: GetMemeUseCase
): ViewModel(){

    private val compositeDisposable = CompositeDisposable()
    private var descriptionLive = MutableLiveData<String>()
    private var linkLive = MutableLiveData<String>()
    private var btnStateLive = MutableLiveData<Boolean>()

    private val descriptions: MutableList<String> = mutableListOf()
    private val images: MutableList<String> = mutableListOf()
    private var counter: Int = 1

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getDescriptionLive(): LiveData<String> {
        return descriptionLive
    }

    fun getLinkLive(): LiveData<String> {
        return linkLive
    }

    fun getBackBtnState(): LiveData<Boolean> {
        return btnStateLive
    }

    private fun getMeme(): Single<MemeModel> {
        return getMemeUseCase.execute()
    }

    fun fetchMeme() {
        compositeDisposable.add(getMeme()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                images.add(it.gifUrl)
                descriptionLive.value = it.description
            }, {
                Log.d(Constants.LOG_TAG, "Error: ${it.message}")
            }))
    }

    fun nextMeme() {
        if(counter >= descriptions.size) {
            fetchMeme()
        }
        linkLive.value = images[counter]
        descriptionLive.value = descriptions[counter]
        counter++
        btnStateLive.value = counter != 0
    }

    fun previousMeme() {
        counter--
        linkLive.value = images[counter]
        descriptionLive.value = descriptions[counter]
        btnStateLive.value = counter != 0
    }
}