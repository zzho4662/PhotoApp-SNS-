package com.jhjang.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jhjang.photoapp.api.NetworkClient;
import com.jhjang.photoapp.api.UserApi;
import com.jhjang.photoapp.model.UserReq;
import com.jhjang.photoapp.model.UserRes;
import com.jhjang.photoapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    EditText edit_email;
    EditText edit_passwd;
    Button btn_login;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_email = findViewById(R.id.edit_email);
        edit_passwd = findViewById(R.id.edit_passwd);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edit_email.getText().toString().trim();
                String passwd = edit_passwd.getText().toString().trim();
                if(email.contains("@") == false){
                    Toast.makeText(Login.this, "이메일형식이 올바르지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(passwd.isEmpty() || passwd.length() < 4 || passwd.length() > 12){
                    Toast.makeText(Login.this, "비밀번호 규칙에 맞지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                // body 셋팅
                UserReq userReq = new UserReq(email, passwd);

                // 네트워크로 데이터 처리
                Retrofit retrofit = NetworkClient.getRetrofitClient(Login.this);
                UserApi userApi = retrofit.create(UserApi.class);

                Call<UserRes> call = userApi.loginUser(userReq);

                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        if(response.isSuccessful()){
                            String token = response.body().getToken();

                            SharedPreferences sp = getSharedPreferences(Utils.PREFERENCES_NAME,
                                    MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", token);
                            editor.apply();

                            Intent i = new Intent(Login.this, Welcome.class);
                            startActivity(i);

                            finish();

                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {

                    }
                });

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}






