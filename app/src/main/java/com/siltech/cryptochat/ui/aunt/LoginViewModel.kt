package com.siltech.cryptochat.ui.aunt

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.siltech.cryptochat.model.User
import com.siltech.cryptochat.network.UpdatePublicKey
import com.siltech.cryptochat.repo.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel constructor(private val repository: Repository) : ViewModel() {
    val userModel = MutableLiveData<List<User>>()
    val body = MutableLiveData<Void>()
    val errorMessage = MutableLiveData<String>()


}
