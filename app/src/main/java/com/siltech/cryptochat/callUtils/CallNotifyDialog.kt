package com.siltech.cryptochat.callUtils

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.NewCallActivity
import com.siltech.cryptochat.databinding.UserCallingDialogueBinding
import com.siltech.cryptochat.webRtcNative.WebRtcCallActivity


class CallNotifyDialog : DialogFragment(R.layout.user_calling_dialogue) {
    lateinit var binding:UserCallingDialogueBinding
    var signalConnectionID =""
    override fun onSaveInstanceState(outState: Bundle) {}

    override fun setupDialog(dialog: Dialog, style: Int) {

        //  super.setupDialog(dialog, style)
        isCancelable = false
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setGravity(Gravity.TOP)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MaterialDialogSheetTransparent)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserCallingDialogueBinding.bind(view)
        binding.acceptCall.setOnClickListener{
            Toast.makeText(requireContext(), "Call Accepted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), WebRtcCallActivity::class.java).apply {
                putExtra("newCall",signalConnectionID)
            })
            dismiss()
        }
        binding.declineCall.setOnClickListener{
            Toast.makeText(requireContext(), "Call Declined", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}