package com.example.money.manager.config;

import com.example.money.manager.security.JwtRequestFilter;
import com.example.money.manager.service.AppUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AppUserDetailService appUserDetailService;
    private final JwtRequestFilter jwtRequestFilter;

@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http.cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable) ///disabling csrf
        .authorizeHttpRequests(auth -> auth.requestMatchers("/check","/health","/register","/activate","/login").permitAll().anyRequest().authenticated())
    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   /// to say spring that dont create session for the user means in every login user
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

    //for passwordencoder
@Bean
    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { /// CORS (Cross-Origin Resource Sharing)
    CorsConfiguration configuration = new CorsConfiguration();   ///React / Frontend ko backend access ki permission dena mtlb hum @Crossorigin bhi use kr skte the but wo contorller level pe kam krna tha lkin ye krn se ye application level pe kam karega

        configuration.setAllowedOriginPatterns(List.of("*"));///Ye decide karta hai kaunse frontend allowed hain.* mean koi bhi origin allowed he
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));    ///Ye batata hai frontend kaunsi HTTP requests bhej sakta hai.
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));   ///this allows the header . agar header allow nhi hota to JWT request block ho jati
        configuration.setAllowCredentials(true);                ///this allow to send the credentials
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();  //// Defines the URL patterns where the CORS configuration will be applied.
        source.registerCorsConfiguration("/**", configuration); ///all the endpoints of the application
        return source;


    }
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(appUserDetailService);

        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

}
