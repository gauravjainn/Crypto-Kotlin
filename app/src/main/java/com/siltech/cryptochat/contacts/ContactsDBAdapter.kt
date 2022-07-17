package com.siltech.cryptochat.contacts

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siltech.cryptochat.R
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatWithSMSDatas

class ContactsDBAdapter(
    var contactsList: List<ChatWithSMSDatas>,
    private val onClick:(ChatWithSMSDatas) -> Unit,
    var changeChat: (ChatWithSMSDatas) -> Unit
) : RecyclerView.Adapter<ContactsDBAdapter.ContactItemHolder>() {
    private var nameOfChat = ""
    fun setListSMS(list: List<ChatWithSMSDatas>) {
        this.contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstContact: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(createModel: ChatWithSMSDatas) {

            if(createModel.owner.nameDB == "name"
            ){
                firstContact.text = createModel.owner.name
            }else{
                firstContact.text = createModel.owner.nameDB
            }
            //
            itemView.findViewById<ImageView>(R.id.image_change).setOnClickListener {
                changeChat(createModel)
            }

            itemView.findViewById<TextView>(R.id.tv_name).setOnClickListener {
                onClick(createModel)
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

    fun setNameOfChat(w:String){
         nameOfChat = w
    }
//    fun deleteItems(contact: Contacts) {
//        contactsList.remove(contact)
//        notifyDataSetChanged()
//    }

//    fun addContact(contact: Contacts) {
//        contactsList.add(contact)
//        notifyDataSetChanged()
//    }
}
