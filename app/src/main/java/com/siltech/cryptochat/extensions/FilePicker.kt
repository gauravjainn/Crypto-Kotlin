package com.siltech.cryptochat.extensions

import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

//class ImagePickerLauncher(
//    private val context: Context,
//    private val resultLauncher: ActivityResultLauncher<Intent>
//) {
//    fun launch(config: BaseConfig = ImagePickerConfig()) {
//        val finalConfig = if (config is ImagePickerConfig) checkConfig(config) else config
//        val intent = createImagePickerIntent(context, finalConfig)
//        resultLauncher.launch(intent)
//    }
//}
//    typealias ImagePickerCallback = (List<Image>) -> Unit
//
//    fun Fragment.registerImagePicker(
//        callback: ImagePickerCallback
//    ): ImagePickerLauncher {
//        return ImagePickerLauncher(requireContext(), createLauncher(callback))
//    }
//
//abstract class BaseConfig {
//    abstract var savePath: ImagePickerSavePath
//    abstract var returnMode: ReturnMode
//    abstract var isSaveImage: Boolean
//}

