package com.example.usermgmt

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean

@TestConfiguration
@EnableFeignClients("com.example.usermgmt")
class TestProjectConfig() {

    @Bean
    fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor = BasicAuthRequestInterceptor("testuser","pass")

}