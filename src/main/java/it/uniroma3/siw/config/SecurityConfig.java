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
                .requestMatchers("/**", "/css/**", "/js/**", "/images/**").permitAll() // Pagine pubbliche
                .anyRequest().authenticated() // Tutte le altre pagine richiedono autenticazione
            )
            .formLogin(form -> form
                .loginPage("/login") // Usa la tua pagina di login personalizzata
                .loginProcessingUrl("/perform-login") // Endpoint personalizzato per il login
                .permitAll()
                .defaultSuccessUrl("/") // Reindirizza alla homepage dopo il login
                .failureUrl("/login?error=true") // Reindirizza a /login?error=true in caso di errore
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // Reindirizza alla homepage dopo il logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}