package com.example.usermgmt

import com.example.usermgmt.core.UserDto
import feign.FeignException.FeignClientException
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

fun ResponseEntity<UserDto>.nullSafeBody(): UserDto = body ?: throw RuntimeException("body should not be null")

fun <T> T.statusCodeFromApiCall(call: (T) -> ResponseEntity<UserDto>): HttpStatusCode =
    try {
        call(this).statusCode
    } catch (e: FeignClientException) {
        HttpStatusCode.valueOf(e.status())
    }