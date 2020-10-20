package com.jhjang.photoapp.api;

import com.jhjang.photoapp.model.PostRes;
import com.jhjang.photoapp.model.UserRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostApi {

    @GET("/api/v1/posts")       // ?offset=0&limit=25
    Call<PostRes> getPosts(@Header("Authorization") String token,
                           @Query("offset") int offset,
                           @Query("limit") int limit);

    // 파일 전송을 가능하게 해준다. => @Multipart
    @Multipart
    @POST("/api/v1/posts")
    Call<UserRes> createPost(@Header("Authorization") String token,
                             @Part MultipartBody.Part file,
                             @Part("content") RequestBody requestBody);
}
