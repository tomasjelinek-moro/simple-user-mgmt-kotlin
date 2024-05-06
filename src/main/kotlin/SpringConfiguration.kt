package com.example.usermgmt

import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class ProjectConfig(val userDetailsService: UserDetailsService) {

    val SWAGGER_URLS = listOf("/swagger-ui/**", "/v3/api-docs/**")

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf {
            it.disable()
        }.authorizeHttpRequests {
            it.requestMatchers("/users/**").authenticated()
            it.requestMatchers(*SWAGGER_URLS.toTypedArray()).permitAll()
            it.anyRequest().permitAll()
        }.authenticationProvider(daoAuthenticationProvider())
            .httpBasic(Customizer.withDefaults())
            .build()

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider =
        DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailsService)
            setPasswordEncoder(passwordEncoder())
        }

    // a dummy implementation of the encoder which keeps the password in cleartext
    @Bean
    fun passwordEncoder(): PasswordEncoder = object : PasswordEncoder {
        override fun encode(rawPassword: CharSequence?): String = rawPassword?.toString() ?: ""

        override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean =
            rawPassword.toString() == encodedPassword
    }

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("public-api").pathsToMatch(*arrayOf("/users/**")).build()

    @Bean
    fun customOpenApi(): OpenAPI = OpenAPI()
        .info(io.swagger.v3.oas.models.info.Info().title("User Management API").version("1.0.0"))
}

@SpringBootApplication
class TrainingProjApplication

fun main(args: Array<String>) {
    runApplication<TrainingProjApplication>(*args)
}
