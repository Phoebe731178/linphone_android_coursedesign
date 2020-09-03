package com.hunter.linphoneandroid.presenter;

import com.hunter.linphoneandroid.base.BasePresenter;
import com.hunter.linphoneandroid.rx.ResponseObserver;
import com.hunter.linphoneandroid.service.TestService;
import com.hunter.linphoneandroid.ui.interfaces.ITestView;
import com.hunter.linphoneandroid.vo.DoubanResponse;


public class TestPresenter extends BasePresenter {
    TestService service;

    @Override
    protected void initService() {
        service = getService(TestService.class);
    }

    public void test(String keyword, final ITestView testView) {

        subscribe(testView, service.test(keyword), new ResponseObserver<DoubanResponse>(testView) {
            @Override
            public void onNext(DoubanResponse response) {
                testView.showData(response.getBooks());
            }
        });
    }

}
