package it.uniroma3.siw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import it.uniroma3.siw.service.UsersService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UsersService usersService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/users/register", "/users/login", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/product/create/**", "/product/edit/**", "/product/delete/**", 
                                        "/users/profile", "/users/products", "/cart/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)  // true = sempre reindirizza alla homepage
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // Mantieni CSRF per le richieste web
                .userDetailsService(usersService);
        return http.build();
    }
}