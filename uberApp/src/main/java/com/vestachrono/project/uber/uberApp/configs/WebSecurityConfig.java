package com.vestachrono.project.uber.uberApp.configs;

import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] PUBLIC_ROUTES = {"/auth/**"};

    @Bean
    SecurityFilterChain securityFilterChai(HttpSecurity httpSecurity) throws Exception {

    httpSecurity
            .sessionManagement(sessionCreation -> sessionCreation
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrfConfig -> csrfConfig.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_ROUTES).permitAll()
                    .anyRequest().authenticated()
            );

        return httpSecurity.build();
    }



}
