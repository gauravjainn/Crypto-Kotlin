package com.siltech.cryptochat.chat

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siltech.cryptochat.databinding.RvItemsMessagesBinding
import com.siltech.cryptochat.getUserLogin
import com.siltech.cryptochat.model.GetMessagesResponseItem
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


class ChatAdapter(
    private val showAction: (GetMessagesResponseItem) -> Unit,
    private val onClick: (String) -> Unit,
    private val encr: (String) -> Unit

) :
    RecyclerView.Adapter<ChatAdapter.SmsVH>() {
    var list = emptyList<GetMessagesResponseItem>()
    var eeencr: String? = null

    private lateinit var diffUtilCalback: DiffUtil.DiffResult

    @SuppressLint("NotifyDataSetChanged")
    fun setListSMS(list: List<GetMessagesResponseItem>) {
        diffUtilCalback = DiffUtil.calculateDiff(DiffUtilCalback(this.list, list))
        diffUtilCalback.dispatchUpdatesTo(this)
        this.list = list
//        notifyDataSetChanged()
    }

    inner class SmsVH(val binding: RvItemsMessagesBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ResourceAsColor")
        fun onBind(a: GetMessagesResponseItem) {
            itemView.setOnLongClickListener {
                showAction(a)
                true
            }

//            binding.playBtn.setOnClickListener {
//                onClick(a)
//            }
//
//            binding.fileTitle.setOnClickListener {
//                openFile(a)
//            }


            if (a.message != null) {
                Log.d("sms+from_servak", "${a.message}")
                encr(a.message.toString())
            }

            when (list[position].type) {
                "text" -> {
                    if (list[position].userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {
                        binding.clCurrentUserText.visibility = View.VISIBLE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.smsCurrentUserText.text = eeencr

                    } else {
                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.VISIBLE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.smsSendUserText.text = eeencr

                    }
                }

                "image" -> {
                    if(list[position].userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {
                        val decodedByte = Base64.decode(eeencr, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.VISIBLE
                        binding.clSendUserImage.visibility = View.GONE

                        Glide.with(binding.clSendUserAudio.context).load(bitmap).into(binding.imageCurrentUserSms)
                    }else{
                        val decodedByte = Base64.decode(eeencr, Base64.DEFAULT)
                        var bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.VISIBLE

                        Glide.with(binding.clSendUserAudio.context).load(bitmap).into(binding.imageSendUserSms)
                    }
                }
                "voice" -> {
                    if(list[position].userLogin == getUserLogin(binding.clCurrentUserAudio.context)) {

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.VISIBLE
                        binding.textAudioCurr.text = eeencr!!.substring(2,4)
                        binding.clSendUserAudio.visibility = View.GONE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.playCurrentUserBtn.setOnClickListener {
                            onClick(eeencr!!)
                        }

                    }else{

                        binding.clCurrentUserText.visibility = View.GONE
                        binding.clSendUserText.visibility = View.GONE

                        binding.clCurrentUserAudio.visibility = View.GONE
                        binding.clSendUserAudio.visibility = View.VISIBLE

                        binding.clCurrentUserImage.visibility = View.GONE
                        binding.clSendUserImage.visibility = View.GONE

                        binding.playSendUserBtn.setOnClickListener {
                            onClick(eeencr.toString())
                        }
                    }


                }

//            if(getUserLogin(binding.smsCurrentUserText.context) == a.userLogin){
//
//                if (eeencr != null && eeencr.toString().length <=500  ){
//                    binding.imageCurrentUserSms.visibility = View.GONE
//                    binding.smsCurrentUserText.visibility = View.VISIBLE
//                    binding.smsCurrentUserText.text = eeencr
////                    /binding.smsText2.visibility = View.GONE
////                    binding.imageSms2.visibility = View.GONE
////                binding.playBtn.setImageURI(eeencr.toString())
//                }else{
//                    binding.imageCurrentUserSms.visibility = View.VISIBLE
//                    binding.smsCurrentUserText.visibility = View.GONE
//
//                    Log.d("eeencr","${eeencr}")
//
//                    binding.smsSendUserText.visibility = View.GONE
//                    binding.imageCurrentUserSms.visibility = View.GONE
//                    val decodedByte = Base64.decode(eeencr, Base64.DEFAULT)
//                    val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
////                binding.playBtn.setImageBitmap(bitmap)
//                    Glide.with(binding.imageSendUserSms.context).load(bitmap).into(binding.imageCurrentUserSms)
//
//                }
//
//            }else{
//
//                if (eeencr != null && eeencr.toString().length <=500  ){
//                    binding.imageSendUserSms.visibility = View.GONE
//                    binding.smsSendUserText.visibility = View.VISIBLE
//                    binding.smsSendUserText.text = eeencr
//                    binding.smsCurrentUserText.visibility = View.GONE
//                    binding.imageCurrentUserSms.visibility = View.GONE
////                binding.playBtn.setImageURI(eeencr.toString())
//                }else{
//                    binding.imageSendUserSms.visibility = View.VISIBLE
//                    binding.smsCurrentUserText.visibility = View.GONE
//                    Log.d("eeencr","${eeencr}")
//                    binding.smsCurrentUserText.visibility = View.GONE
//                    binding.imageCurrentUserSms.visibility = View.GONE
//                    val decodedByte = Base64.decode(eeencr, Base64.DEFAULT)
//                    val bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
////                binding.playBtn.setImageBitmap(bitmap)
//                    Glide.with(binding.imageCurrentUserSms.context).load(bitmap).into(binding.imageSendUserSms)
//
//                }
//
//            }


//            var cipher: Cipher? = null
//
//            val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
//            keyGenerator.init(128) // block size is 128bits
//            val secretKey: SecretKey = keyGenerator.generateKey()
//            cipher = Cipher.getInstance("AES") //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)
//
//            val decoder: Base64.Decoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Base64.getDecoder()
//            } else {
//                TODO("VERSION.SDK_INT < O")
//            }
//
//            val mfstr = a.message
//
//            var carg: String = URLDecoder.decode(mfstr, "UTF-8")
//
//            carg = carg.replace("\n", "")
//
//            val v5 = Base64.getDecoder().decode(carg.toByteArray())
//
//            val encryptedTextByte: ByteArray = decoder.decode( v5.toString())
//            cipher.init(Cipher.DECRYPT_MODE, secretKey)
//            val decryptedByte: ByteArray =
//                cipher.doFinal(encryptedTextByte)
//            binding.smsText.text = decryptedByte.toString()
//          val secretKey =   getSecretKey(itemView.context)
//
//            var cipher : Cipher? = null
//            cipher =
//                Cipher.getInstance("AES") //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)
//            val decoder = Base64.getDecoder()
//            val encryptedTextByte = decoder.decode(a.message.toString())
//            cipher!!.init(Cipher.DECRYPT_MODE, secretKey)
//            val decryptedByte = cipher.doFinal(encryptedTextByte)
//            val mDe = String(decryptedByte)
//            Log.d("aoalo",mDe.toString())
//            binding.smsText.text = mDe
//            if(a.texts != null) {

//            }else {
//
//            }

//            if(a.texts != null){
//
//                binding.playBtn.visibility = View.GONE
//            }
//
//            if (a.isSMSText == true) {
//                binding.playBtn.visibility = View.GONE
//                binding.smsText.visibility = View.VISIBLE
//                binding.smsText.text = a.texts
//                binding.timeOfVoice.visibility = View.GONE
//            }else{
//                binding.playBtn.visibility = View.VISIBLE
//                binding.smsText.visibility = View.GONE
//                binding.timeOfVoice.visibility = View.VISIBLE
//            }
//            binding.voiceTitle.text = a.names
//
//
//            if(a.isDoc == true){
//
//                binding.fileTitle.visibility = View.VISIBLE
//                binding.timeOfVoice.visibility = View.GONE
//                binding.playBtn.visibility = View.GONE
//            }else{
//                binding.fileTitle.visibility = View.GONE
//
//            }
//
//            if (a.length != null) {
//                val minutes = TimeUnit.MILLISECONDS.toMinutes(a.length!!)
//                val seconds =
//                    TimeUnit.MILLISECONDS.toSeconds(a.length!! - TimeUnit.MINUTES.toSeconds(minutes))
//                binding.timeOfVoice.text = String.format("%02d:%02d", minutes, seconds)
//
//            } else {
////                binding.timeOfVoice.visibility = View.GONE
////                binding.playBtn.visibility = View.GONE
////                binding.voiceTitle.visibility = View.GONE
////                binding.fileTitle.visibility = View.VISIBLE
//            }
//
//            val c = Calendar.getInstance()
//
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val hour = c.get(Calendar.HOUR_OF_DAY)
//            val minute = c.get(Calendar.MINUTE)
//            val myLdt = LocalDateTime.of(year, month, day, hour, minute)
//
//            binding.tvCurrentTime.text = a.id.toString()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsVH {
        val binding =
            RvItemsMessagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsVH(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SmsVH, position: Int) {
        holder.onBind(list[position])

//        9eECFbKN04oyYOvCu57vIw==
    }

    override fun getItemCount(): Int {
        return list.size
    }
fun setdecr(string: String){
    eeencr = string
}
    fun decrypt() {

    }
    @Throws(FileNotFoundException::class, IOException::class)
    private fun load(file: String): ByteArray? {
        val fis = FileInputStream(file)
        val buf = ByteArray(fis.available())
        fis.read(buf)
        fis.close()
        return buf
    }




}