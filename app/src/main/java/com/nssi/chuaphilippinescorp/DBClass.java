package com.nssi.chuaphilippinescorp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class DBClass extends SQLiteOpenHelper {

    ////
    private static final String TABLENAME = "BARCODE_TBL";
    private static final String ID = "ID";
    private static final String SUPPLIERNAME = "SUPPLIERNAME";
    private static final String BARCODE = "BARCODE";
    private static final String QUANTITY = "QUANTITY";
    private static final String DATESTAMP = "DATESTAMP";
    private static final String STATUS = "STATUS";

    private static final String USERTABLE = "userTbl";
    private static final String PASSWORD = "password";

    public DBClass(Context context) {
        super(context, "ChuaDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table " + TABLENAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SUPPLIERNAME + " TEXT," +
                BARCODE + " TEXT," +
                QUANTITY + " TEXT," +
                DATESTAMP + " TEXT," +
                STATUS + " TEXT)");
        db.execSQL("Create table " + USERTABLE + " (" + PASSWORD + " TEXT)");

        db.execSQL("insert into " + USERTABLE + "(" + PASSWORD + ") values ('adminadmin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + USERTABLE);
        onCreate(db);
    }

    public Boolean insertItem(ItemModel model) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String date = df.format(Calendar.getInstance().getTime());

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("Select * from " + TABLENAME + " where " + SUPPLIERNAME + " ='" + model.getSupplierName() +
                    "' and " + BARCODE + " ='" + model.getBarcode() +
                    "' and " + DATESTAMP + " LIKE '" + date + "%'", null);

            Log.d("TAG", "insertItem: " + cursor.getCount());
            if (cursor.getCount() != 0) {
                db.execSQL("update "+TABLENAME+" set "+QUANTITY+" = "+QUANTITY+"+"+model.getQuantity()+" where "+
                        SUPPLIERNAME + " ='" + model.getSupplierName() +
                        "' and " + BARCODE + " ='" + model.getBarcode() +
                        "' and " + DATESTAMP + " LIKE '" + date + "%'");
                return true;
            }
            ContentValues val = new ContentValues();
            val.put(SUPPLIERNAME, model.getSupplierName());
            val.put(BARCODE, model.getBarcode());
            val.put(QUANTITY, model.getQuantity());
            val.put(DATESTAMP, model.getDateStamp());
            val.put(STATUS, "NO");
            long res = db.insert(TABLENAME, null, val);
            Log.d("TAG", "insertItem: " + res);
            return res > -1;
        } catch (Exception e) {
            return false;
        }
    }

    public int grandTotal(String supplierName, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(" + QUANTITY + ") from " + TABLENAME + " where " +
                SUPPLIERNAME + " ='" + supplierName + "' and " + DATESTAMP + " LIKE '" + date + "%'", null);
        String total = "";
        while (res.moveToNext())
            total = res.getString(0);
        if (total != null)
            return Integer.parseInt(total);
        else
            return 0;
    }

    public String getPassword() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select " + PASSWORD + " from " + USERTABLE, null);
        if (res.moveToNext())
            return res.getString(0);
        else
            return "";
    }

    public Boolean updatePassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD, password);
        int res = db.update(USERTABLE, values, null, null);
        return res > 0;
    }

    public ArrayList<ItemModel> exportData(String dateTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select " + SUPPLIERNAME + "," + BARCODE + "," +
                QUANTITY + "," + DATESTAMP + " from " + TABLENAME + " where " + STATUS + " ='NO' and " +
                DATESTAMP + " LIKE '" + dateTime + "%' " +
                " order by " + SUPPLIERNAME + " ASC", null);

        ArrayList<ItemModel> list = new ArrayList<>();
        while (res.moveToNext())
            list.add(new ItemModel(res.getString(0), res.getString(1),
                    res.getString(2), res.getString(3)));
        res.close();
        return list;
    }

    public void updateItem(String suplierName, String barcode, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update " + TABLENAME + " Set " + STATUS + " = 'YES' where " + SUPPLIERNAME + " = '" + suplierName + "' and " +
                BARCODE + " ='" + barcode + "' and " + DATESTAMP + " ='" + dateTime + "'");
    }

    public void deleteItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLENAME + " where " + ID + " = '" + id + "'");
    }
    public void afterDownload(String supplier) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLENAME + " where " + SUPPLIERNAME + " = '" + supplier + "'");
    }

    public ArrayList<String> supplierName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select distinct " + SUPPLIERNAME + " From " + TABLENAME, null);
        ArrayList<String> list = new ArrayList<>();
        while (res.moveToNext())
            list.add(res.getString(0));
        res.close();
        return list;
    }

    public ArrayList<ItemModel> itemListPerSupplier(String supplierName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLENAME +
                " where " + SUPPLIERNAME + " ='" + supplierName + "'", null);
        ArrayList<ItemModel> list = new ArrayList<>();
        while (res.moveToNext())
            list.add(new ItemModel(res.getString(0), res.getString(1), res.getString(2),
                    res.getString(3), res.getString(4)));
        res.close();
        return list;
    }

    public ArrayList<ItemModel> itemListPerSupplier(String supplierName, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLENAME +
                " where " + SUPPLIERNAME + " ='" + supplierName + "' and " + DATESTAMP + " LIKE '" + date + "%'", null);
        ArrayList<ItemModel> list = new ArrayList<>();
        while (res.moveToNext())
            list.add(new ItemModel(res.getString(0), res.getString(1), res.getString(2),
                    res.getString(3), res.getString(4)));
        res.close();
        return list;
    }
    public Boolean isDownloaded(String supplierName, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLENAME +
                " where " + SUPPLIERNAME + " ='" + supplierName + "' and " + DATESTAMP + " LIKE '" + date + "%' and "+STATUS+" = 'NO'", null);
        Log.d("TAG", "isDownloaded: "+res.getCount());
        return res.getCount() != 0;
    }
}
