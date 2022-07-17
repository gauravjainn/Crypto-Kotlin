package com.siltech.cryptochat.call


import android.content.Context
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.siltech.cryptochat.R

import android.content.*
import android.media.ToneGenerator
import android.view.View




//import kotlinx.android.synthetic.main.activity_call.*





import com.siltech.cryptochat.call.util.SoundManager
import com.siltech.cryptochat.call.util.Const
import android.media.MediaPlayer
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.siltech.cryptochat.databinding.ActivityChatBinding



class CallActivity : AppCompatActivity() {



    private lateinit var binding: ActivityChatBinding

    var mPlayer: MediaPlayer? = null



    override fun onStart() {


        var mp = MediaPlayer.create(applicationContext, R.raw.call);
        mp.start();


        super.onStart()
    }

    private var mReceiver: DataReceiver? = null

    companion object {
        fun create(context: Context, name: String, isIncoming: Boolean) {
            val intent = Intent(context, CallActivity::class.java)
            intent.putExtra(Const.KEY_IS_INCOMING, isIncoming)
            intent.putExtra(Const.KEY_NAME, name)
            context.startActivity(intent)
        }
    }

    private fun isIncoming(): Boolean = intent.getBooleanExtra(Const.KEY_IS_INCOMING, false)

    // Registers receiver.
    // Also starts ringing for incoming call.
    // And starts dial tones for outgoing call.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //val greenButton = findViewById<View>(R.id.greenButton) as ImageButton

        val soundManager = SoundManager.getInstance(this)
        val filter = IntentFilter(Const.ACTION_DATA_TO_ACTIVITY_EXCHANGE)


        unregisterReceiver()
        mReceiver = DataReceiver()
        registerReceiver(mReceiver, filter)

        if (isIncoming()) {
            soundManager.startRinging()
        } else {
            soundManager.startTone(ToneGenerator.TONE_SUP_RINGTONE)
        }

        updateCallStatus(Const.SipCall.STARTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    // Prevents going back with hardware button.
    override fun onBackPressed() { }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    fun updateCallStatus(status: Const.SipCall) {
        val greenButton = Button(this)
        val redButton = Button(this)
        val name = TextView(this)

        when (status) {
            Const.SipCall.STARTED -> {
                if (isIncoming()) {
                    greenButton.text = getString(R.string.call_answer)
                    redButton.text = getString(R.string.call_decline)

                    greenButton.setOnClickListener {
                        greenButton.visibility = View.GONE
                        redButton.text = getString(R.string.call_end)
                        sendBroadcast(getIntentForStatusOfCall(Const.SipCall.ANSWERED))
                    }
                } else {
                    greenButton.visibility = View.GONE
                    redButton.text = getString(R.string.call_end)
                }

                name.text = intent.getStringExtra(Const.KEY_NAME)

                redButton.setOnClickListener {
                    sendBroadcast(getIntentForStatusOfCall(Const.SipCall.ENDED))
                    finish()
                }
            }
            Const.SipCall.ENDED -> finish()
        }
    }

    private fun getIntentForStatusOfCall(status: Const.SipCall): Intent {
        val intent = Intent()

        intent.action = Const.ACTION_DATA_TO_SERVICE_EXCHANGE
        intent.putExtra(Const.KEY_STATUS, status)
        return intent
    }

    private fun unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }


}