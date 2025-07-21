package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login", "/register", "/ws/**",
                                                                "/join-private-room", "/invite/join", "/invite/respond")
                                                .permitAll()
                                                .requestMatchers("/dashboard", "/profile", "/chat", "/private-chat",
                                                                "/room/**", "/profile/**", "/message/delete/**",
                                                                "/room/delete/**", "/private/message/delete/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout") // ✅ URL untuk logout
                                                .logoutSuccessUrl("/login") // ✅ Redirect ke halaman login setelah
                                                                            // logout
                                                .deleteCookies("JSESSIONID") // ✅ Hapus cookies
                                                .invalidateHttpSession(true) // ✅ Invalidasi sesi
                                                .permitAll())
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/login", "/logout",
                                                "/message/delete/selected", "/message/delete/all",
                                                "/profile/update", "/room/**", "/private/message/delete/all",
                                                "/private/message/delete/selected", "/invite/join", "/invite/respond"));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // ✅ Pastikan sama dengan waktu register
        }
}