package com.siltech.cryptochat.chat.sheet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.siltech.cryptochat.R
import com.siltech.cryptochat.chat.Chat
import com.siltech.cryptochat.databinding.FragmentBottomSheetBinding
import com.siltech.cryptochat.support.SupportActivity.Companion.REQUEST_CODE_PICKER


class BottomFragment(): BottomSheetDialogFragment() {
    
    override fun onCreateView(
        
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val OPERATION_CHOOSE_PHOTO = 2


       // var binding= inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        val binding = FragmentBottomSheetBinding.inflate(layoutInflater)
        val view = binding.root

        binding.btnAttachImage.setOnClickListener {
            Log.d("tag1", "ClickedImage")
            // Navigation.findNavController(view).navigate(R.id.action_firstView_to_secondView)

            if (getContext()?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.RECORD_AUDIO
                    )
                } != PackageManager.PERMISSION_GRANTED && getContext()?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(getContext() as Activity, permissions, 0)
            } else {

                Intent(Intent.ACTION_PICK).also {
                    val intent = Intent("android.intent.action.GET_CONTENT")
                    intent.type = "image/*"
                    //startActivity(Intent(requireContext(), Chat::class.java).putExtra("OPERATION_CHOOSE_PHOTO", OPERATION_CHOOSE_PHOTO))
                    getActivity()?.startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
                    dismiss()

                }
            }

        }


    // binding.attachFile.setOnClickListener {

    //     if (getContext()?.let { it1 ->
    //             ContextCompat.checkSelfPermission(
    //                 it1,
    //                 Manifest.permission.RECORD_AUDIO
    //             )
    //         } != PackageManager.PERMISSION_GRANTED && getContext()?.let { it1 ->
    //             ContextCompat.checkSelfPermission(
    //                 it1,
    //                 Manifest.permission.WRITE_EXTERNAL_STORAGE
    //             )
    //         } != PackageManager.PERMISSION_GRANTED
    //     ) {
    //         val permissions = arrayOf(
    //             android.Manifest.permission.RECORD_AUDIO,
    //             android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    //             android.Manifest.permission.READ_EXTERNAL_STORAGE
    //         )
    //         ActivityCompat.requestPermissions(getContext() as Activity, permissions, 0)
    //     } else {
    //         Intent(Intent.ACTION_PICK).also {
    //             val intent = Intent("android.intent.action.GET_CONTENT")
    //             intent.type = "*/*"
    //             val mimetypes = arrayOf(
    //                 "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    //                 "application/msword", "application/msword", "application/vnd.ms-powerpoint","application/vnd.ms-excel","application/x-wav",
    //                 "application/pdf"
    //             )

    //             intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

    //             getActivity()?.startActivityForResult(intent, REQUEST_CODE_PICKER)
//  //           loadingDialog = showDialog("Загрузка файла...")
    //             dismiss()

    //         }

    //     }
    //     Log.d("tag2", "ClickedFILE")
    //     //Navigation.findNavController(view).navigate(R.id.action_firstView_to_secondView)
    // }

        return view
    }
}

