package com.melardev.spring.rxsecjwt.dtos.responses;

public class SuccessResponse extends AppResponse {


    public SuccessResponse(String message) {
        super(true);
        addFullMessage(message);
    }

    public SuccessResponse() {
        this(null);
    }
}
