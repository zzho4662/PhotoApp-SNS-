package com.jhjang.photoapp.api;


import com.jhjang.photoapp.model.UserReq;
import com.jhjang.photoapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

// 레트로핏 라이브러리 사용할때는 인터페이스로 먼저 선언
public interface UserApi {

    // http 메소드와 경로 설정
    // Call 에 <> 안에 들어갈 것은, 네트워크 통해서 받아온 데이터를 처리할 클래스
    // 메소드의 파라미터에는, 보낼 데이터를 처리할 클래스

    @POST("/api/v1/users")
    Call<UserRes> createUser(@Body UserReq userReq);

    @POST("/api/v1/users/login")
    Call<UserRes> loginUser(@Body UserReq userReq);

    @DELETE("/api/v1/users/logout")
    Call<UserRes> logoutUser(@Header("Authorization") String token);

}
