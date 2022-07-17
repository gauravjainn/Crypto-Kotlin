package com.siltech.cryptochat.chat

import androidx.recyclerview.widget.DiffUtil
import com.siltech.cryptochat.model.GetMessagesResponseItem

class DiffUtilCalback(
    private val oldList: List<GetMessagesResponseItem>,
    private val newList: List<GetMessagesResponseItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].message == newList[newItemPosition].message

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}