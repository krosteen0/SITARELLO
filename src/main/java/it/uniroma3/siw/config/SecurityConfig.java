package it.uniroma3.siw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/registration", "/css/**", "/js/**", "/images/**").permitAll() // Pagine pubbliche
                .anyRequest().authenticated() // Tutte le altre pagine richiedono autenticazione
            )
            .formLogin(form -> form
                .loginPage("/login") // Pagina di login personalizzata
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // Reindirizza alla homepage dopo il logout
                .permitAll()
            );

        return http.build();
    }
}