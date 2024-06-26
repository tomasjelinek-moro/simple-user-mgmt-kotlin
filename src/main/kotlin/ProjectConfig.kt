package com.example.usermgmt

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class ProjectConfig(val userDetailsService: UserDetailsService) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf {
            it.disable()
        }.authorizeHttpRequests {
            it.requestMatchers("/users-protected/**").authenticated()
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

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("public-api").pathsToMatch("/users/**").build()

    @Bean
    fun protectedApi(): GroupedOpenApi =
        GroupedOpenApi.builder().group("protected-api").pathsToMatch("/users-protected/**").build()

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(Info().title("User Management API").version("0.0.1"))
        .addSecurityItem(SecurityRequirement().addList("basicAuth")).components(
            Components().addSecuritySchemes(
                "basicAuth",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
            )
        )
}
