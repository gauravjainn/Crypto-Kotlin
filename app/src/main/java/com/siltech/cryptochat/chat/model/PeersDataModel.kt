package com.siltech.cryptochat.chat.model

class PeersDataModel : ArrayList<PeersDataModelItem>()

data class PeersDataModelItem(
    val id: Int,
    val is_blocked: Boolean,
    val login: String,
    val public_key: String,
    val role: String,
    val socket_id: String,
    val fcm_token: String,
    val on_call: Any,
)