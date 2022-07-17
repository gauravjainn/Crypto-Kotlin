package com.siltech.cryptochat.settings


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.siltech.cryptochat.R
import com.siltech.cryptochat.databinding.ActivitySettingsBinding
import android.content.pm.ActivityInfo
import androidx.core.content.ContextCompat



class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNW = cm.activeNetworkInfo
        return if (activeNW != null && activeNW.isConnected) {
            true
        } else false
    }


    override fun onStart() {
        if (hasConnection(this)) {
            Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show()
        super.onStart()
    }

    var apply: Button? = null
    
    var first_pin: EditText? = null
    var second_pin: EditText? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = ContextCompat.getColor(this@SettingsActivity, R.color.light_blue)
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Settings"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)




        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.savechangesBtn.setOnClickListener {
            // val pin_one: String = this.first_pin.getText().toString()
            var pin_one = binding.update1pinEditText.text.toString()
            var pin_two = binding.update2pinEditText.text.toString()
            //val pin_two: String = second_pin.getText().toString()
             if (!pin_one.equals(pin_two)) {
                    Toast.makeText(getApplicationContext(), "Пинкоды не совпдают", Toast.LENGTH_SHORT).show();
                } else {
                 if (pin_one == pin_two && pin_one.length >= 5 && pin_two.length >= 5) {
                val settings = getSharedPreferences("PREFS1", 0)
                val editor = settings.edit()
                editor.putString("password1", pin_one)
                editor.apply()
                Toast.makeText(
                    applicationContext,
                    "Ваш новый PIN успешно сохранён",
                    Toast.LENGTH_SHORT
                ).show()

            }
            if (pin_two.isEmpty() || pin_one.isEmpty()) {
                Toast.makeText(applicationContext, "Поля пусты", Toast.LENGTH_SHORT).show()
            }
            if (pin_one == "" || pin_two == "") {
                //(txt_pass_two.isEmpty() || txt_pass_one.isEmpty()) {
                Toast.makeText(applicationContext, "Поля пусты", Toast.LENGTH_SHORT).show()
            }
            if (pin_one.length <= 5 || pin_two.length <= 5) {
                Toast.makeText(
                    applicationContext,
                    "Минимальная длина пароля 5 символов",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        }


//        binding.wipeDataBtn.setOnClickListener {
//            CreateAlertDialoge();
//
//
//            /**AlertDialog.Builder(context)
//                .setTitle("Delete entry")
//                .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
//                // The dialog is automatically dismissed when a dialog button is clicked.
//                .setPositiveButton(android.R.string.yes,
//                    DialogInterface.OnClickListener { dialog, which ->
//                        // Continue with delete operation
//                    }) // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show()**/
//        }


    }

//
//    private fun CreateAlertDialoge() {
//        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        builder.setMessage("Вы уверены что хотите очистить данные? \nЭто действие необратимо.")
//        builder.setPositiveButton("Да", object : DialogInterface.OnClickListener {
//            override fun onClick(dialog: DialogInterface?, which: Int) {
//                clearAppData()
//            }
//        })
//        builder.setNegativeButton("Нет", object : DialogInterface.OnClickListener {
//            override fun onClick(dialog: DialogInterface?, which: Int) {
//
//            }
//        })
//        builder.create()
//        builder.show()
//    }

//    private fun clearAppData() {
//        try {
//            // clearing app data
//            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
//                (getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData() // note: it has a return value!
//            } else {
//                val packageName = applicationContext.packageName
//                val runtime = Runtime.getRuntime()
//                runtime.exec("pm clear $packageName")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
