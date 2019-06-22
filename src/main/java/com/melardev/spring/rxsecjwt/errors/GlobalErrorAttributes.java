package com.melardev.spring.rxsecjwt.errors;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  boolean includeStackTrace) {
        //Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("status", super.getErrorAttributes(request, includeStackTrace).get("status"));
        map.put("full_messages", Collections.singletonList(this.getError(request).getMessage()));

        return map;
    }
}
