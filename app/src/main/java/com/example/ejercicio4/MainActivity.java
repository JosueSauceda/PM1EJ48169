package com.example.ejercicio4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ejercicio4.configuracion.SQLiteConexion;
import com.example.ejercicio4.tablas.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int peticion_captura_imagen = 100;
    static final int peticion_acceso_cam = 201;

    ImageView ObjetoImagen;
    Button btntakephoto, btnguardar;
    String PathImagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ObjetoImagen = (ImageView) findViewById(R.id.imageView);
        btntakephoto = (Button) findViewById(R.id.btntakephoto);
        btnguardar = (Button) findViewById(R.id.btnguardar);

        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

    btnguardar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AgregarImagen();
        }
    });
    }

    private void permisos() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, peticion_acceso_cam);
        }else {
            TakePhotoDir();
        }
    }
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == peticion_acceso_cam) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TakePhotoDir();
                }
            }
        }

    /*private void tomarfoto() {
        Intent intentfoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentfoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentfoto, peticion_captura_imagen);
        }
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

     /* if (requestCode == peticion_captura_imagen) {
            Bundle extras = data.getExtras();
            Bitmap imagen = (Bitmap) extras.get("data");
            ObjetoImagen.setImageBitmap(imagen);
        }*/
    if (requestCode == peticion_captura_imagen){
        File foto = new File(PathImagen);
        ObjetoImagen.setImageURI(Uri.fromFile(foto));
    }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */);
        // Save a file: path for use with ACTION_VIEW intents
        PathImagen = image.getAbsolutePath();
        return image;
    }

    private void TakePhotoDir() {
        Intent Intenttakephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Intenttakephoto.resolveActivity(getPackageManager()) != null) {
            File foto = null;
            try {
                foto = createImageFile();
            } catch (Exception ex) {
                ex.toString();
            }
            if (foto != null) {
                Uri fotoUri = FileProvider.getUriForFile(this, "com.example.ejercicio4.fileprovider", foto);
                Intenttakephoto.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(Intenttakephoto, peticion_captura_imagen);
            }
        }
    }
        private void AgregarImagen() {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();
            Bitmap bitmap = ObjetoImagen.getDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , baos);
            byte[] blob = baos.toByteArray();

            ContentValues valores = new ContentValues();

            valores.put(Transacciones.img, blob.toString());

            Long resultado = db.insert(Transacciones.TbImagenes, Transacciones.id, valores);

            Toast.makeText(getApplicationContext(), "Registro Ingresado " + resultado.toString()
                    , Toast.LENGTH_SHORT).show();

            db.close();

            ClearScreen();
        }

        private void ClearScreen()
        {
            ObjetoImagen.setImageURI(Uri.EMPTY);
        }

    }
