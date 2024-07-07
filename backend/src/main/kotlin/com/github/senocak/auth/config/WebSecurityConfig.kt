package com.github.senocak.auth.config

import com.github.senocak.auth.security.JwtAuthenticationEntryPoint
import com.github.senocak.auth.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val unauthorizedHandler: JwtAuthenticationEntryPoint,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    /**
     * Override this method to configure the HttpSecurity.
     * @param http -- It allows configuring web based security for specific http requests
     * @throws Exception -- throws Exception
     */
    @Profile("!integration-test")
    @Throws(Exception::class)
    @Bean
    fun securityFilterChainDSL(http: HttpSecurity): SecurityFilterChain =
        http {
            csrf { disable() }
            exceptionHandling { authenticationEntryPoint = unauthorizedHandler }
            httpBasic { disable() }
            authorizeRequests {
                authorize(pattern = "/api/v1/auth/**", access = permitAll)
                authorize(pattern = "/api/v1/public/**", access = permitAll)
                authorize(pattern = "/api/v1/swagger/**", access = permitAll)
                authorize(pattern = "/swagger**/**", access = permitAll)
                authorize(pattern = "/error**/**", access = permitAll)
                authorize(matches = anyRequest, access = authenticated)
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            headers { frameOptions { disable() } }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(filter = jwtAuthenticationFilter)
        }
        .run { http.build() }
}
