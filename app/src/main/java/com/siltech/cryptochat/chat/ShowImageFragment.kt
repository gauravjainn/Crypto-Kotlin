package com.siltech.cryptochat.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.siltech.cryptochat.R


class ShowImageFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_show_image, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    val img = view.findViewById<ImageView>(R.id.image_p)

//        img!!.requestFocus()
        var image = arguments?.getString("title")
        if (image != null) {
            Log.d("IMAGEEEEEEEE",image)

        val decodedByte = Base64.decode(image, Base64.DEFAULT)
        var bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
//
//        Glide.with(requireContext()).load(convertStringToBitmap(image)
//        ).into(view.findViewById<ImageView>(R.id.image_p))
////
//
img.setImageBitmap(convertStringToBitmap(image))
        }


    }

    companion object {
        fun newInstance(title: String?): ShowImageFragment {
            val frag = ShowImageFragment()
            val args = Bundle()
            args.putString("title", title)
            frag.setArguments(args)
            return frag
        }
    }

    fun convertStringToBitmap(string: String?): Bitmap? {
        val byteArray1: ByteArray
        byteArray1 = Base64.decode(string, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(
            byteArray1, 0,
            byteArray1.size
        ) /* w  w  w.ja va 2 s  .  c om*/
    }
}