package com.siltech.cryptochat.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.siltech.cryptochat.R;
import com.siltech.cryptochat.app.AppModule;
import com.siltech.cryptochat.contacts.HomeActivityKotlin;


public class Test extends AppCompatActivity {


    Button next;
    Button end;
    EditText one_pass;
    EditText two_pass;

    Integer userId;
    String userLogin;

    private final static String FILE_NAME = "auth.txt";

    /** public boolean hasConnection(final Context context) {
     ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
     NetworkInfo activeNW = cm.getActiveNetworkInfo();
     if (activeNW != null && activeNW.isConnected()) {
     return true;
     }
     return false;
     }**/

    @Override
    protected void onStart() {
/**
 if(hasConnection(this)) {
 Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show();
 }
 else  Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show();
 **/
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.light_blue));
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        next = findViewById(R.id.btnConfirm);
        // end = findViewById(R.id.button6);
        one_pass = findViewById(R.id.set_password);
        two_pass = findViewById(R.id.second_confirmation);

        Intent intent = getIntent();

        userId = intent.getIntExtra("cr_id", 0);
        userLogin = intent.getStringExtra("user_login");

        System.out.println(userId);
        System.out.println(userLogin);

        Log.d("userid",userId.toString());

        next.setOnClickListener(view -> {
            String txt_pass_one = one_pass.getText().toString();
            //Integer txt_pass_one = one_pass.toInt();
            // Integer txt_pass_two = one_pass.toInt();
            String txt_pass_two = two_pass.getText().toString();

            //  int txt_pass_one = Integer.parseInt(txt_pass_one_str);
            //  int txt_pass_two = Integer.parseInt(txt_pass_two_str);
            // if(txt_pass_one.equals(txt_pass_two) && !txt_pass_one.isEmpty() && !txt_pass_two.isEmpty()  ){
            if (((txt_pass_one.length() < 5) || (txt_pass_two.length() < 5))) {
                Toast.makeText(getApplicationContext(), "Минимальная длина пароля 5 символов", Toast.LENGTH_SHORT).show();
            } else {
                if (!txt_pass_one.equals(txt_pass_two)) {
                    Toast.makeText(getApplicationContext(), "Пинкоды не совпдают", Toast.LENGTH_SHORT).show();
                } else {
                    if (txt_pass_one.equals("") || txt_pass_two.equals("")) {
                        Toast.makeText(getApplicationContext(), "Не введены пароли", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txt_pass_one.equals(txt_pass_two) && ((txt_pass_one.length() >= 5) && (txt_pass_two.length() >= 5))) {
                            //if ((txt_pass_one == txt_pass_two) &&((txt_pass_one !=0)&&(txt_pass_two!=0)) ) {
                            //save pin code
                            SharedPreferences settings = getSharedPreferences("PREFS1", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("password1", txt_pass_one);
                            editor.apply();
                            AppModule appModule = new AppModule();
                            appModule.connectToSocket();
                            // enter the app
                            Intent intent1 = new Intent(getApplicationContext(), HomeActivityKotlin.class);
                            intent1.putExtra("cr_id", userId);
                            intent1.putExtra("user_login", userLogin);
                            startActivity(intent1);
                            finish();


                            // Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_SHORT).show();


                            //create auth file

                            /**FileOutputStream fos = null;
                             try {
                             String text = "User";
                             fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                             fos.write(text.getBytes());
                             //  Toast.makeText(getApplicationContext(), "Файл сохранен", Toast.LENGTH_SHORT).show();
                             // startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                             //  finish();
                             } **//**catch (IOException ex) {
                             Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                             } finally {
                             try {
                             if (fos != null)
                             fos.close();
                             } catch (IOException ex) {
                             Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                             }**/

                        }
                        if (txt_pass_one.equals("") || txt_pass_two.equals("")) {
                            //(txt_pass_two.isEmpty() || txt_pass_one.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Поля пусты", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            }
        });


    }




}