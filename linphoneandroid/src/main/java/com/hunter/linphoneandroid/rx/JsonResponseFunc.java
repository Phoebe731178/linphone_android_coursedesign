package com.hunter.linphoneandroid.rx;


import com.hunter.linphoneandroid.exception.ApiException;
import com.hunter.linphoneandroid.vo.JsonResponse;

import io.reactivex.functions.Function;


/**
 * RxJava map转换
 *
 * @param <T>
 */
public class JsonResponseFunc<T> implements Function<JsonResponse<T>, T> {
    @Override
    public T apply(JsonResponse<T> tJsonResponse) throws Exception {
        if (tJsonResponse == null) return null;

        if (!tJsonResponse.isSuccess()) {
            throw new ApiException(tJsonResponse);
        }

        return tJsonResponse.getData();
    }
}
