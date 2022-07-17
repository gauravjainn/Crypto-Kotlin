package com.siltech.cryptochat.call

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.siltech.cryptochat.R
import com.siltech.cryptochat.call.util.toast
import org.greenrobot.eventbus.Subscribe
//import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus

import com.siltech.cryptochat.call.util.*
import com.siltech.cryptochat.call.*
import com.siltech.cryptochat.call.events.CallEvent
import com.siltech.cryptochat.call.util.*
//import kotlinx.android.synthetic.main.activity_call_finder.*


class Call_finder : AppCompatActivity() {


    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNW = cm.activeNetworkInfo
        return activeNW != null && activeNW.isConnected
    }


    override fun onStart() {
        if (hasConnection(this)) {
            Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show()
        super.onStart()
    }

    private var mService: CallService? = null
    private var mCallReceiver: CallReceiver? = null
    private var mDataReceiver: DataReceiver? = null
    private val mConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as CallService.CallBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) { }
    }


    companion object {
        private const val PERMISSIONS_BEFORE_REGISTER: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_finder)
        checkCallPermissions()

        //final Button callButton = findViewById(R.id.callButton);
        val callButton: Button = findViewById(R.id.callButton) as Button
        val name: EditText = findViewById(R.id.name) as EditText

        // Start call if name of companion was inputted.
        callButton.setOnClickListener {
            if (name.text.isNotEmpty()) {
                makeCall()
            } else {
                toast(getString(R.string.no_name_error))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //name.showKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mService != null) {
            unbindService(mConnection)
        }

        unregisterCallReceiver()

        EventBus.getDefault().unregister(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_BEFORE_REGISTER) {
            var isPermissionGranted = true
            if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    isPermissionGranted = isPermissionGranted && (result == PackageManager.PERMISSION_GRANTED)
                }
            } else {
                isPermissionGranted = false
            }

            tryToRegister(isPermissionGranted)
        }
    }

    // This method will get registration status from the service.
    // Also you need to unregister the receiver here because it isn't needed anymore.
    fun receiveRegisterState(status: Const.SipRegistration, errorCode: Int) = when (status) {
        Const.SipRegistration.STARTED -> registrationStarted()
        Const.SipRegistration.FINISHED -> registrationFinished()
        Const.SipRegistration.ERROR -> registrationError(errorCode)
    }

    private fun registrationStarted() {
        toast(getString(R.string.sip_registration_started))
    }

    private fun registrationFinished() {
        unregisterDataReceiver()
        registerCallReceiver()

        val callButton = Button(this)

        val name = TextView(this)


        callButton.isEnabled = true
        toast(getString(R.string.sip_registration_finished, Const.SIP_LOGIN))
    }

    private fun registerCallReceiver() {
        val filter = IntentFilter(Const.ACTION_INCOMING_CALL)

        unregisterCallReceiver()
        mCallReceiver = CallReceiver()
        registerReceiver(mCallReceiver, filter)
    }

    private fun unregisterCallReceiver() {
        if (mCallReceiver != null) {
            unregisterReceiver(mCallReceiver)
            mCallReceiver = null
        }
    }

    private fun registrationError(errorCode: Int) {
        unregisterDataReceiver()

        toast(getString(R.string.sip_registration_error, errorCode))
    }

    @Subscribe // Catches event of incoming call.
    // Then tries to take call and show call activity.
    fun onIncomingCallEvent(event: CallEvent) {
        val nameText = mService?.takeAudioCall(event.intent)
        val isStarted = nameText?.isNotEmpty() ?: false

        if (isStarted) {
            CallActivity.create(this, nameText!!, true)
        } else {
            toast(getString(R.string.sip_call_error))
        }
    }

    private fun checkCallPermissions() {
        val permissions = ArrayList<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.USE_SIP)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.size > 0) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), PERMISSIONS_BEFORE_REGISTER
            )
        } else {
            tryToRegister(true)
        }
    }

    // Register user if all permissions was granted.
    // We'll do it via service so let's bind it and register the receiver.
    private fun tryToRegister(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            registerDataReceiver()
            bindService(Intent(this, CallService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        } else {
            finish()
        }
    }

    private fun registerDataReceiver() {
        val filter = IntentFilter(Const.ACTION_DATA_TO_ACTIVITY_EXCHANGE)

        unregisterDataReceiver()
        mDataReceiver = DataReceiver()
        registerReceiver(mDataReceiver, filter)
    }

    private fun unregisterDataReceiver() {
        if (mDataReceiver != null) {
            unregisterReceiver(mDataReceiver)
            mDataReceiver = null
        }
    }

    // Tries to make call and show call activity.
    private fun makeCall() {

        val name = TextView(this)
        val nameText = name.text.toString()
        val sipAddress = "sip:$nameText@${Const.SIP_URL}"
        val isStarted = mService?.makeAudioCall(sipAddress) ?: false

        if (isStarted) {
            CallActivity.create(this, nameText, false)
        } else {
            toast(getString(R.string.sip_call_error))
        }
    }
}