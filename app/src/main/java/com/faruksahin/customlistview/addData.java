package com.faruksahin.customlistview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class addData extends AppCompatActivity {

    private Bitmap selectedPhoto;
    public static SQLiteDatabase database;
    private SQLiteStatement statement;
    private Intent intent;
    private ImageView imageView;
    private EditText txtTitle,txtDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        imageView = findViewById(R.id.imageView);
        txtTitle = findViewById(R.id.editText);
        txtDescription = findViewById(R.id.editText2);
    }
    public void photoSelect(View view)
    {
        //Check Permission
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else
        {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //User Allow Permission
        if(requestCode == 1)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Photo Process
        if(requestCode == 2)
        {
            if(resultCode == RESULT_OK&&data!=null)
            {
                Uri uri = data.getData();
                try {
                    selectedPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    imageView.setImageBitmap(selectedPhoto);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void save(View view)
    {
        try
        {
           database = this.openOrCreateDatabase("myDatabase",MODE_PRIVATE,null);
           database.execSQL("CREATE TABLE IF NOT EXISTS myData(text1 Varchar, text2 Varchar, image BLOB)");
           String sql = "INSERT INTO myData(text1, text2, image) VALUES(?, ?, ?)";
           ByteArrayOutputStream stream = new ByteArrayOutputStream();
           selectedPhoto.compress(Bitmap.CompressFormat.PNG,50,stream);
           byte[] arrays = stream.toByteArray();
           statement = database.compileStatement(sql);
           statement.bindString(1,txtTitle.getText().toString());
           statement.bindString(2,txtDescription.getText().toString());
           statement.bindBlob(3,arrays);
           statement.execute();
           Toast.makeText(this,"Process is Succesfully",Toast.LENGTH_LONG).show();
            new CountDownTimer(1 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    addData.this.finish();
                }
            }.start();
        }catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
