package com.jhjang.photoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhjang.photoapp.api.NetworkClient;
import com.jhjang.photoapp.api.PostApi;
import com.jhjang.photoapp.model.UserRes;
import com.jhjang.photoapp.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Posting extends AppCompatActivity {

    Button btnCamera;
    Button btnGallery;
    EditText editContent;
    Button btnPosting;
    ImageView imgPhoto;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        editContent = findViewById(R.id.editContent);
        btnPosting = findViewById(R.id.btnPosting);
        imgPhoto = findViewById(R.id.imgPhoto);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(
                        Posting.this, Manifest.permission.CAMERA);

                if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Posting.this,
                            new String[]{Manifest.permission.CAMERA} ,
                            1000);
                    Toast.makeText(Posting.this, "카메라 권한 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(i.resolveActivity(Posting.this.getPackageManager())  != null  ){

                        // 사진의 파일명을 만들기
                        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        photoFile = getPhotoFile(fileName);

                        Uri fileProvider = FileProvider.getUriForFile(Posting.this,
                                "com.jhjang.photoapp.fileprovider", photoFile);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        startActivityForResult(i, 100);

                    } else{
                        Toast.makeText(Posting.this, "이폰에는 카메라 앱이 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23){
                    if(checkPermission()){
                        displayFileChoose();
                    }else{
                        requestPermission();
                    }
                }
            }
        });

        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editContent.getText().toString().trim();
                if (content.isEmpty() || photoFile == null){
                    Toast.makeText(Posting.this, "", Toast.LENGTH_SHORT).show();
                    return;


                }
                Retrofit retrofit = NetworkClient.getRetrofitClient(Posting.this);
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("photo",
                        photoFile.getName(), fileBody);
                RequestBody textBody = RequestBody.create(MediaType.parse("text/plain"), content);


                PostApi postApi = retrofit.create(PostApi.class);

                SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME, MODE_PRIVATE);
                String token = sp.getString("token", null);

                Call<UserRes> call = postApi.createPost("Bearer "+ token, part, textBody);

                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        if (response.isSuccessful()){
                            if (response.body().isSuccess()){
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {

                    }
                });



            }
        });

    }
    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(Posting.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(Posting.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(Posting.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(Posting.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return true;
        }else{
            return false;
        }
    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1000: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Posting.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Posting.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Posting.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Posting.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 100 && resultCode == RESULT_OK){
            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imgPhoto.setImageBitmap(photo);
        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri imgPath = data.getData();
            photoFile = new File(imgPath.getPath());
            imgPhoto.setImageURI(imgPath);

            // 실제 경로를 몰라도, 파일의 내용을 읽어와서, 임시파일 만들어서 서버로 보낸다.
            String id = DocumentsContract.getDocumentId(imgPath);
            try {
                InputStream inputStream = getContentResolver().openInputStream(imgPath);
                photoFile = new File(getCacheDir().getAbsolutePath()+"/"+id+".jpg");
                writeFile(inputStream, photoFile);
//                String filePath = photoFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    // 파일의 내용을 읽어와서, 임시파일 만들기 위함.
    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

}






