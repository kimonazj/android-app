package com.example.w7_p3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn;
    EditText edt;
    ImageView img;

    int RANDMAX = 5;
    int randint;
    String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        edt = (EditText)findViewById(R.id.editText);
        img = (ImageView)findViewById(R.id.img);

        retrieveSharedPreferenceInfo();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randint = 1 + (int)(Math.round((Math.random() * RANDMAX)));
                int resImg = getResources().getIdentifier("p" + randint, "drawable", getPackageName());
                img.setImageResource(resImg);
            }
        });

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    input = s.toString();
                }else{
                    input = "";
                }
            }
        });
    }
    //SHARED PREFERENCE STUFF
    void saveSharedPreferenceInfo(){
        //1. Refer to the SharedPreference Object.
        SharedPreferences simpleAppInfo = getSharedPreferences("ActivityOneInfo", Context.MODE_PRIVATE);
        //Private means no other Apps can access this.

        //2. Create an Shared Preferences Editor for Editing Shared Preferences.
        //Note, not a real editor, just an object that allows editing...

        SharedPreferences.Editor editor = simpleAppInfo.edit();

        //3. Store what's important!  Key Value Pair, what else is new...
        editor.putInt("randint", randint);
        editor.putString("input", input);

        //4. Save your information.
        editor.apply();

        //Toast.makeText(this, "Shared Preference Data Updated.", Toast.LENGTH_LONG).show();
    }


    void retrieveSharedPreferenceInfo(){
        
        SharedPreferences simpleAppInfo = getSharedPreferences("ActivityOneInfo", Context.MODE_PRIVATE);

//        String s1 = simpleAppInfo.getString("btnClickMe", "<missing>");
//        String s2 = simpleAppInfo.getString("edtText", "<missing>");
//        String s3 = simpleAppInfo.getString("txtView", "<missing>");

        //Retrieving data from shared preferences hashmap.
        randint = simpleAppInfo.getInt("randint", 1);  //The second parm is the default value, eg, if the value doesn't exist.
        input = simpleAppInfo.getString("input", "");
        edt.setText(simpleAppInfo.getString("input", ""));        //Shared Preferences use internal memory, not SD.
//

//        Toast.makeText(this, s1, Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onDestroy() {
        saveSharedPreferenceInfo();
        super.onDestroy();
    }
}