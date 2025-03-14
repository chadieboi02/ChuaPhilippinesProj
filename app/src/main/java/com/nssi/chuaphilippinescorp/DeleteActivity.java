package com.nssi.chuaphilippinescorp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteActivity extends AppCompatActivity {
    Spinner spinner;
    RecyclerView recyclerView;
    Button btnDelete;
    DBClass db;
    TextView bar,qty,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        db = new DBClass(this);
       spinner = findViewById(R.id.spinner);
       recyclerView  = findViewById(R.id.recyclerview);
       btnDelete  = findViewById(R.id.btn_delete);
       bar = findViewById(R.id.bar);
       qty = findViewById(R.id.qty);
       date = findViewById(R.id.date);
       setSpinner();


        Globalclass.for_all = recyclerView.getWidth() / 3;
        bar.setWidth(recyclerView.getWidth() / 3);
        qty.setWidth(recyclerView.getWidth() / 3);
        date.setWidth(recyclerView.getWidth() / 3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(DeleteActivity.this, parent.getSelectedItem()+"", Toast.LENGTH_SHORT).show();
                setRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDelete.setOnClickListener(v -> {
            if (Globalclass.selectedIds.size() == 0){
                Toast.makeText(this, "Please select item/s to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i =0;i<Globalclass.selectedIds.size();i++){
                db.deleteItem(Globalclass.selectedIds.get(i));
                Log.d("TAG", "onCreate: "+Globalclass.selectedIds.get(i));
            }
            Globalclass.selectedIds = new ArrayList<>();
            setRecyclerView();
        });
    }

    private void setRecyclerView(){
        ArrayList<ItemModel> list = db.itemListPerSupplier(spinner.getSelectedItem().toString());
        recyclerView.setAdapter(new ItemAdaptor(list,DeleteActivity.this));
    }

    private void setSpinner(){
        ArrayList<String> list = db.supplierName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom_item,list);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(DeleteActivity.this,MainActivity.class));
//        finish();
//        super.onBackPressed();
//
//    }
}