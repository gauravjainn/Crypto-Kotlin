package com.siltech.cryptochat.repo

import com.siltech.cryptochat.model.User
import com.siltech.cryptochat.network.Api
import com.siltech.cryptochat.network.UpdatePublicKey
import retrofit2.Call
import retrofit2.Response

class Repository constructor(private val retrofitService: Api) {

    fun create(token: String, user: User): Call<Void>? {
        return retrofitService.createPost("Bearer $token", user)
    }
//
//    fun getUser(token: String, user: String): Response<List<User>> {
//        return retrofitService.getUser("Bearer $token", "eq.$user")
//    }
//
//    fun updateLogin(token: String, user: UpdatePublicKey): Call<List<User>>{
//        return retrofitService.updateLoginPublicKey("Bearer $token", user,"eq.${user.id}")
//    }
}
