package com.siltech.cryptochat.contacts.addNewGroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.contacts.HomeActivityKotlin
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.data.modules.trustAllCerts
import com.siltech.cryptochat.databinding.ActivityAddNewChatBinding
import com.siltech.cryptochat.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class AddNewChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewChatBinding
    private val vm: ChatViewModel by viewModel()

    private val userLogin: String
        get() = intent.getStringExtra("user_login").toString()

    private val creatorId: Int
        get() = intent.getIntExtra("cr_id", 0)

    private var chatForResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()

        vm.state.observe(this) {
            when (it) {
                is State.LoadingState -> {
                    if (it.isLoading) {
                    } else {
                    }
                }
                is State.ErrorState -> {
                    ""
                }
                is State.SuccessListState<*> -> {
                }

                is State.SuccessObjectState<*> -> {
                    when (it.data) {

                        is CreateUserResponse -> {

                            if (it.data.isNotEmpty()) {

                                if (chatForResult) {
                                    chatForResult = false
                                    Toast.makeText(
                                        this,
                                        "Такой чат уже существует",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {

                                    Log.e("TAG","else createNewChat return=representation "+"$userLogin-${binding.etNameOfChat.text}"+" creatorId "+creatorId);

                                    vm.createNewChat(
                                        "return=representation", ChatCreateModel(
                                            "$userLogin-${binding.etNameOfChat.text}",
                                            creatorId
                                        )
                                    )
                                }

                            } else {
                                Toast.makeText(
                                    this,
                                    "Такого пользователя не существует!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        is ChatsListResponse -> {
                            if (it.data.isNotEmpty()) {
                                chatForResult = true

                                Log.e("TAG","if getSpecificUserByLogin return=representation "+"eq." + binding.etNameOfChat.text);

                                vm.getSpecificUserByLogin(
                                    "return=representation",
                                    "eq." + binding.etNameOfChat.text
                                )

                            } else {

                                Log.e("TAG","else getSpecificUserByLogin return=representation "+"eq." + binding.etNameOfChat.text);

                                vm.getSpecificUserByLogin(
                                    "return=representation","eq." + binding.etNameOfChat.text
                                )

                            }
                        }

                        is AddUsersResponse -> {
                        }

                        is CreateChatResponse -> {

                        }

                        is ChangeNameOfChatResponse -> {
                        }

                        is DeletedChatResponse -> {
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnAddChat.setOnClickListener {
            val text = binding.etNameOfChat.text
            if (binding.etNameOfChat.text.isNotEmpty() && text.toString() != userLogin) {

                Log.e("TAG","initListeners getChatForResult "+"eq." + "${userLogin}-${binding.etNameOfChat.text}");

                vm.getChatForResult("eq." + "${userLogin}-${binding.etNameOfChat.text}")
            } else {
                Toast.makeText(this, "Вы не можете добавить самого себя!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

}
