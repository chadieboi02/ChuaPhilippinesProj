package com.nssi.chuaphilippinescorp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText edtSupplier, edtBarcode, edtQuantity;
    TextView txtTotal;
    Button btnSave, btnDownload, btnDelete;
    DBClass db;
    File sdcard = Environment.getExternalStorageDirectory();
    RecyclerView recyclerView;
    private String supplierName = "";
    ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        writeReadExternal();
        initialize();
        listener();
    }

    private void listener() {

        edtQuantity.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_UP || (event.getKeyCode() != KeyEvent.KEYCODE_ENTER
                    && keyCode != EditorInfo.IME_ACTION_DONE && keyCode != EditorInfo.IME_ACTION_SEARCH))
                return false;

            String supplierName = edtSupplier.getText().toString().trim();
            String barcode = edtBarcode.getText().toString().trim();
            String quantity = edtQuantity.getText().toString().trim();
            if (supplierName.isEmpty()) {
                edtSupplier.setError("Required!");
                edtSupplier.requestFocus();
                return true;
            } else if (barcode.isEmpty()) {
                edtBarcode.setError("Required!");
                edtBarcode.requestFocus();
                return true;
            } else if (quantity.isEmpty()) {
                edtQuantity.setError("Required!");
                edtQuantity.requestFocus();
                return true;
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            String date = df.format(Calendar.getInstance().getTime());
            ItemModel model = new ItemModel();
            model.setBarcode(barcode);
            model.setSupplierName(supplierName);
            model.setQuantity(quantity);
            model.setDateStamp(date);
            if (db.insertItem(model)) {
                Toast.makeText(MainActivity.this, "Successfully Save", Toast.LENGTH_SHORT).show();
                txtTotal.setText(grandTotal());
                edtQuantity.setText("");
                edtBarcode.setText("");
                edtBarcode.requestFocus();
            } else
                Toast.makeText(MainActivity.this, "The item is already scanned!", Toast.LENGTH_SHORT).show();
            setRecyclerView();
            return true;
        });
        edtSupplier.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !edtSupplier.getText().toString().isEmpty()) {
                txtTotal.setText(grandTotal());
                setRecyclerView();
                edtSupplier.setEnabled(false);
                supplierName = edtSupplier.getText().toString().trim();
            }
        });
        btnSave.setOnClickListener(v -> {
            if (db.isDownloaded(supplierName, date())) {
                Toast.makeText(this, Html.fromHtml("<font color='#FE0501'>Please Download the item under this Supplier Name before create new reference</font>"), Toast.LENGTH_SHORT).show();
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 200);
                return;
            }
            newSupplier();
        });
        btnDownload.setOnClickListener(v -> {
            if (edtSupplier.getText().toString().isEmpty()) {
                Toast.makeText(this, Html.fromHtml("<font color='#FE0501'>Please enter supplier name</font>"), Toast.LENGTH_SHORT).show();
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 200);
                edtSupplier.setError("Required!");
                edtSupplier.requestFocus();
                return;
            }
            DateFormat dd = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
            String xdate = dd.format(Calendar.getInstance().getTime());
            ArrayList<ItemModel> list = db.exportData(date());
            if (list.size() == 0) {
                Toast.makeText(this, "No Data!", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer strbuff = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (i != 0 && !list.get(i).getSupplierName().equals(list.get(i - 1).getSupplierName())) {
                    generateNoteOnSD("item" + xdate + "_" + list.get(i - 1).getSupplierName() + ".txt", strbuff.toString());
                    strbuff = new StringBuffer();
                }
                strbuff.append("'" + list.get(i).getSupplierName() + "';;")
                        .append(list.get(i).getBarcode() + ";")
                        .append(list.get(i).getQuantity() + ";;")
                        .append(list.get(i).getDateStamp() + "\r\n");
                db.updateItem(list.get(i).getSupplierName(), list.get(i).getBarcode(), list.get(i).getDateStamp());
                if ((list.size() - i) == 1)
                    generateNoteOnSD("item" + xdate + "_" + list.get(i).getSupplierName() + ".txt", strbuff.toString());
            }
            db.afterDownload(supplierName);
        });

        btnDelete.setOnClickListener(v -> showPasswordPrompt());
    }

    private void newSupplier() {
        edtSupplier.setEnabled(true);
        edtSupplier.setText("");
        txtTotal.setText("0");
        edtSupplier.requestFocus();
        setRecyclerView();
    }

    private void initialize() {
        db = new DBClass(this);
        recyclerView = findViewById(R.id.recyclerview);
        edtBarcode = findViewById(R.id.edtBarcode);
        edtSupplier = findViewById(R.id.edtSupplierName);
        edtQuantity = findViewById(R.id.edtQuantity);
        txtTotal = findViewById(R.id.txtGrandTotal);
        btnDelete = findViewById(R.id.btnDelete);
        btnDownload = findViewById(R.id.btnDownload);
        btnSave = findViewById(R.id.btnSave);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setRecyclerView() {
        ArrayList<ItemModel> list = db.itemListPerSupplier(edtSupplier.getText().toString(), date());
        recyclerView.setAdapter(new ListAdaptor(list, this));
    }

    private String grandTotal() {
        String supplierName = edtSupplier.getText().toString().trim();
        return String.valueOf(db.grandTotal(supplierName, date()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_setting) {
            changePassword();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.updatepassword, null);
        alert.setView(view);
        EditText edtOldPass = view.findViewById(R.id.edtoldPassword),
                edtNewPass = view.findViewById(R.id.edtnewPassword),
                edtConfrimPass = view.findViewById(R.id.edtConfirmPassword);
        Button btnSave = view.findViewById(R.id.btn_save),
                btnCancel = view.findViewById(R.id.btn_cancel);

        AlertDialog dialog = alert.create();
        btnSave.setOnClickListener(v -> {
            String oldPass = edtOldPass.getText().toString();
            String newPass = edtNewPass.getText().toString();
            String conPass = edtConfrimPass.getText().toString();

            if (!oldPass.equals(db.getPassword())) {
                edtOldPass.setError("Old password is incorrect!");
                edtOldPass.requestFocus();
                return;
            }
            if (newPass.isEmpty()) {
                edtNewPass.setError("Required!");
                edtNewPass.requestFocus();
                return;
            } else if (!newPass.equals(conPass)) {
                edtConfrimPass.setError("Password does not matched!");
                edtConfrimPass.requestFocus();
                return;
            }
            if (db.updatePassword(newPass)) {
                Toast.makeText(this, "Password is successfully updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else
                Toast.makeText(this, Html.fromHtml("<font color='#F71D03'>Unsuccessful Transaction</font>"), Toast.LENGTH_SHORT).show();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showPasswordPrompt() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.passwordprompt, null);
        alert.setView(view);
        EditText edtPassword = view.findViewById(R.id.edtPass);
        Button btnOk = view.findViewById(R.id.btn_ok),
                btnCancel = view.findViewById(R.id.btn_cancel);
        AlertDialog dialog = alert.create();
        dialog.show();
        btnOk.setOnClickListener(v -> {
            String password = edtPassword.getText().toString();
            if (password.equals(db.getPassword())) {
                startActivity(new Intent(MainActivity.this, DeleteActivity.class));
//                finish();
            } else {
                edtPassword.setError("Incorrect Password!");
                edtPassword.requestFocus();
                return;
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    public void generateNoteOnSD(String fileName, String sBody) {
        try {
            File myFile = new File(sdcard + File.separator + "Download", fileName);
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(sBody);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            newSupplier();
        } catch (IOException e) {
            Log.d("TAG", "generateNoteOnSD: " + e.getMessage());
        }
    }


    private void writeReadExternal() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        23);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        23);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    private String date() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

}