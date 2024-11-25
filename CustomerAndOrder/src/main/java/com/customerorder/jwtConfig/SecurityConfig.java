package com.customerorder.jwtConfig;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {    
        http
            // Stateless session management because we're using JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Securing API endpoints and allowing others
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/api/admin/**").hasAnyRole("ADMIN")	
                .requestMatchers("/api/**").authenticated()  // Only /api/** needs authentication
                .anyRequest().permitAll()                    // Other endpoints are open
            )
            
            // Adding JWT token validation filter before basic authentication
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
            
            // Disable CSRF as we're relying on JWT for security
            .csrf(csrf -> csrf.disable())
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Allow basic authentication and form login (optional)
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());
        
        return http.build();
   }

   private CorsConfigurationSource corsConfigurationSource() {
	
		return new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				
				CorsConfiguration cfg = new CorsConfiguration();
				cfg.setAllowedOrigins(Arrays.asList(
						     "http://localhost:3000"
						));
				cfg.setAllowedMethods(Collections.singletonList("*"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(Collections.singletonList("*"));
				cfg.setExposedHeaders(Arrays.asList("Authorization"));
				cfg.setMaxAge(3600L);
				
				return cfg;
			}
		};
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}