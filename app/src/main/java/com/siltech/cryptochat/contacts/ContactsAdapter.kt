package com.siltech.cryptochat.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.R
import com.siltech.cryptochat.model.UsersChatResponse
import com.siltech.cryptochat.model.UsersChatResponseItem

class ContactsAdapter(
    var contactsList: List<UsersChatResponseItem>,
    private val addNewChat: (UsersChatResponseItem) -> Unit,
    private val changeName: (UsersChatResponseItem) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactItemHolder>() {

    fun setListSMS(list: List<UsersChatResponseItem>) {
        this.contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstContact: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(createModel: UsersChatResponseItem) {
            firstContact.text = createModel.chatName
            itemView.setOnClickListener {
                addNewChat(createModel)
            }
            itemView.findViewById<ImageView>(R.id.image_change).setOnClickListener {
             changeName(createModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactItemHolder, position: Int) {
        holder.bind(contactsList[position])
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

//    fun deleteItems(contact: Contacts) {
//        contactsList.remove(contact)
//        notifyDataSetChanged()
//    }

//    fun addContact(contact: Contacts) {
//        contactsList.add(contact)
//        notifyDataSetChanged()
//    }

        fun deleteItem ( i : Int ){

        }























}
