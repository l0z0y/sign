package com.yy.sign;

import com.yy.sign.model.SignRequest;
import com.yy.sign.model.SignResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpService {


    /**
     * SignTest Request
     *
     * @param request  SignRequest
     * @return SignResponse
     */
    @POST("/api/user/neibudaka")
    Call<SignResponse> signTest(@Body SignRequest request);


}
