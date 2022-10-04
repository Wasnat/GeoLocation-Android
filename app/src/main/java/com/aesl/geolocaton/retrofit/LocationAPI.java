package com.aesl.geolocaton.retrofit;

import com.aesl.geolocaton.model.LocationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LocationAPI {

    @GET("/location/all")
    Call<List<LocationModel>> getAllLocation();

    @POST ("/location/save")
    Call<LocationModel> save(@Body LocationModel location);




}
