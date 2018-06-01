package com.faruksahin.customlistview;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Bitmap> photoFromDatabase;
    private ArrayList<String> titleFromDatabase;
    private ArrayList<String> descriptionFromDatabase;
    private custom_ListView customAdapter;
    private ListView lstView;
    private Intent intent;
    private AlertDialog.Builder alert;
    private SQLiteStatement statement;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Main Thread
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lstView = findViewById(R.id.listView);
        get_data();
        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Are You Sure?");
                alert.setMessage(titleFromDatabase.get(position).toString()+" is Delete?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    try
                    {
                        addData.database = MainActivity.this.openOrCreateDatabase("myDatabase",MODE_PRIVATE,null);
                        addData.database.execSQL("CREATE TABLE IF NOT EXISTS myData(text1 Varchar, text2 Varchar, image BLOB)");
                        String sql = "DELETE FROM myData WHERE text1=? and text2=?";
                        statement = addData.database.compileStatement(sql);
                        statement.bindString(1,titleFromDatabase.get(position));
                        statement.bindString(2,descriptionFromDatabase.get(position));
                        statement.execute();
                        get_data();
                        Toast.makeText(MainActivity.this,"Delete is Succes!",Toast.LENGTH_LONG).show();
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this,ex.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    get_data();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Create Menu
        MenuInflater infaler = this.getMenuInflater();
        infaler.inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Menu Item Select
        if(item.getItemId() == R.id.menu_add)
        {
            intent = new Intent(getApplicationContext(),addData.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.menu_refresh)
        {
            get_data();
        }
        return super.onOptionsItemSelected(item);
    }

    public void get_data()
    {
        //Database Process
        try
        {
            addData.database = this.openOrCreateDatabase("myDatabase",MODE_PRIVATE,null);
            addData.database.execSQL("CREATE TABLE IF NOT EXISTS myData(text1 Varchar, text2 Varchar, image BLOB)");
            Cursor cursor = addData.database.rawQuery("SELECT * FROM myData",null);
            photoFromDatabase = new ArrayList<>();
            titleFromDatabase = new ArrayList<>();
            descriptionFromDatabase = new ArrayList<>();
            customAdapter = new custom_ListView(photoFromDatabase,titleFromDatabase,descriptionFromDatabase,this);
            lstView.setAdapter(customAdapter);
            int photoIx = cursor.getColumnIndex("image");
            int titleIx = cursor.getColumnIndex("text1");
            int descIx = cursor.getColumnIndex("text2");
            cursor.moveToFirst();
            Toast.makeText(this, "Refresh Data Success", Toast.LENGTH_SHORT).show();
            while(cursor!=null)
            {
                byte[] bytes = cursor.getBlob(photoIx);
                Bitmap photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                photoFromDatabase.add(photo);
                titleFromDatabase.add(cursor.getString(titleIx));
                descriptionFromDatabase.add(cursor.getString(descIx));
                cursor.moveToNext();
                customAdapter.notifyDataSetChanged();
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
