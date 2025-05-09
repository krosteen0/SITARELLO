package it.uniroma3.siw.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserSettingsDTO {
    @NotBlank(message = "L'username è obbligatorio")
    @Size(min = 3, max = 50, message = "L'username deve essere tra 3 e 50 caratteri")
    private String username;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Inserisci un'email valida")
    private String email;

    @Size(max = 100, message = "La password non può superare i 100 caratteri")
    private String password;

    @Size(max = 100, message = "La conferma password non può superare i 100 caratteri")
    private String confirmPassword;

    // Getter e Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}