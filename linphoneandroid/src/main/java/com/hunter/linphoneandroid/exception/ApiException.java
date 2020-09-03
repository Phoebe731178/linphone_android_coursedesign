package com.hunter.linphoneandroid.exception;


import com.hunter.linphoneandroid.vo.JsonResponse;

/**
 * 自定义异常
 *r
 */
public class ApiException extends RuntimeException {
    private JsonResponse tJsonResponse;

    public ApiException(JsonResponse jsonResponse) {
        super(jsonResponse.getMessage());
        tJsonResponse = jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        tJsonResponse = jsonResponse;
    }

    public JsonResponse getJsonResponse() {
        return tJsonResponse;
    }
}
