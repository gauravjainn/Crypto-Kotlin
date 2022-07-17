package com.siltech.cryptochat.chat.chat_users

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.contacts.ContactsDBAdapter
import com.siltech.cryptochat.data.State
import com.siltech.cryptochat.databinding.ActivityChatUsersBinding
import com.siltech.cryptochat.getUserIsAdmin
import com.siltech.cryptochat.model.*
import com.siltech.cryptochat.saveUserIsAdmin
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatUsersActivity : AppCompatActivity() {

    private lateinit var adapter: ChatUsersAdapter
    private val viewModel: ChatViewModel by viewModel()

    private val chat_name: String?
        get() = intent.getStringExtra("chat_name")

    private val chat_id: Int?
        get() = intent.getIntExtra("chatID", 0)

    private val userChatIdAdmin: Int?
        get() = intent.getIntExtra("userChatID", 0)

    private var userID: Int? = null

    private var isAdmin = false

    private var userChatId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Пользователи"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val binding = ActivityChatUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getUsersChat("eq." + chat_name.toString())

        adapter = ChatUsersAdapter{
            viewModel.getSpecificUserByLogin("return=representation","eq."+it.userLogin )
            deleteUser(it.isAdministrator)
    }
        initRequests()

        binding.rvChatUsers.adapter = adapter
    }

    private fun deleteUser(isAdministrator:Boolean) {
        val builder = AlertDialog.Builder(this)
        Toast.makeText(this, "$userChatId+$userChatIdAdmin", Toast.LENGTH_SHORT).show()

        builder
            .setPositiveButton("Да") { dialog, id1 ->

                if (getUserIsAdmin(this)) {
                    viewModel.deleteUser("return=representation", "eq." + userChatId.toString())
                }else{
                    Toast.makeText(this, "Невозможно удалить пользователя. Вы не являетесь администратором!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Нет") { dialog, id2 ->
                dialog.dismiss()
            }
        builder.setTitle("Вы уверены, что хотите удалить пользователя??")
        builder.setCancelable(false)
        builder.show()
    }

    private fun initRequests() {
        viewModel.state.observe(this) {
            when (it) {
                is State.LoadingState -> {
                    if (it.isLoading) {
                        ""
                    } else {
                        ""
                    }
                }
                is State.ErrorState -> {
                    ""
                }
                is State.SuccessListState<*> -> {
                    when (if (it.data.isEmpty()) null else it.data[0]) {
                        is GetChatUsersResponseItemX -> {
                            Log.d("aa", "Succes")
                            adapter.setListSMS(it.data as ArrayList<GetChatUsersResponseItemX>)
                        }
                    }
                }

                is State.SuccessObjectState<*> -> {
                    when (it.data) {
                        is DeleteUserResponse -> {
                            viewModel.getUsersChat("eq." + chat_name.toString())
                        }

                        is GetChatUsersResponseX -> {

                            Log.d("chatsss", "AAAAAAAAAAAAAAAAaasdas")
                        }

                        is CreateUserResponse -> {
                            Toast.makeText(this, "${it.data}", Toast.LENGTH_SHORT).show()
                            viewModel.getUserIdInChat("return=representation", "eq." + it.data[0].id, "eq." + chat_id.toString())
                        }

                        is GetUserIdInChatResponse -> {
                            userChatId = it.data[0].id
                        }

                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
