package com.sononpos.communityviwerex.Funtional;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitExService {
    String URL = "http://jsonplaceholder.typicode.com";

    @GET("/posts/{userId}")
    Call<RFData> getData(@Path("userId") String userId);
}
