package com.balashenka.comrade.configuration;

import com.balashenka.comrade.controller.ApiPath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.balashenka.comrade"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(@NonNull HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable().authorizeRequests()
                .antMatchers(ApiPath.API_V2_COMRADE_MESSAGES_PATH + "/**").permitAll()
                .antMatchers(ApiPath.API_V2_COMRADE_ATTACHMENTS_PATH + "/**").permitAll()
                .antMatchers(ApiPath.API_V2_COMRADE_MEMBERSHIPS_PATH + "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
