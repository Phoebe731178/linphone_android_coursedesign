package com.hunter.linphoneandroid.rx;


import com.hunter.linphoneandroid.rx.ResponseObserver;
import com.hunter.linphoneandroid.base.IBaseView;
import com.hunter.linphoneandroid.exception.ApiException;
import com.hunter.linphoneandroid.vo.JsonResponse;

public abstract class JsonResponseObserver<T> extends ResponseObserver<T> {
    public JsonResponseObserver(IBaseView baseView) {
        super(baseView);
    }

    @Override
    public void onNext(T t) {
        if (t instanceof JsonResponse) {
            JsonResponse response = (JsonResponse) t;
            if (!response.isSuccess()) {
                onError(new ApiException(response));
            }else{
                onSuccess(t);
            }
        }
    }

    public abstract void onSuccess(T t);
}
