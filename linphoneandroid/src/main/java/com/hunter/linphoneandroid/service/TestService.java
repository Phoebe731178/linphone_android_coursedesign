package com.hunter.linphoneandroid.service;

import com.hunter.linphoneandroid.app.URLs;
import com.hunter.linphoneandroid.vo.DoubanResponse;
import com.hunter.linphoneandroid.vo.JsonResponse;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface TestService {
    @GET(URLs.MODUEL_BOOK + URLs.SEARCH)
    Observable<DoubanResponse> test(@Query("q") String keyword);
}
