package com.blueberryjoy.novel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class UnmatchedRequestController {
    @RequestMapping("/**")
    public void unmatchedRequest() {
        throw new ApiException("ENDPOINT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}