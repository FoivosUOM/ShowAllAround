package com.example.showallaround.intefraces;

import com.example.showallaround.object.classes.Hashtag;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {

    @GET("place.json")
    Call<List<Hashtag>> getHashtags(
            @Query("id") String id
    );

}
