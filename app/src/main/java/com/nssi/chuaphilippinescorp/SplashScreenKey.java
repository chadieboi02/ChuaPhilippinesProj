package com.nssi.chuaphilippinescorp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SplashScreenKey extends AppCompatActivity {

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;
    EditText edtKey;
    Button btnOkay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_key);

        edtKey = findViewById(R.id.edtKey);
        btnOkay = findViewById(R.id.btnOkay);

        CheckLogin();

        edtKey.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                    keyCode == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String key = edtKey.getText().toString().trim();
                if (key.equals("NSSI-2023")) {
                    DoLogin(key);
                }else{
                    Toast.makeText(this, Html.fromHtml("<font color='#FE0501'>Invalid key code</font>"), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
        btnOkay.setOnClickListener(v -> {
            String key = edtKey.getText().toString().trim();
            if (key.equals("NSSI-2023")) {
                DoLogin(key);
            }else{
                Toast.makeText(this, Html.fromHtml("<font color='#FE0501'>Invalid key code</font>"), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void DoLogin(String key) {
        try {
            if (shp == null)
                shp = getSharedPreferences("myPreferences", MODE_PRIVATE);
            shpEditor = shp.edit();
            shpEditor.putString("Key", key);
            shpEditor.commit();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void CheckLogin() {
        if (shp == null)
            shp = getSharedPreferences("myPreferences", MODE_PRIVATE);

        String key = shp.getString("Key", "");

        if (key != null && !key.equals("")) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}