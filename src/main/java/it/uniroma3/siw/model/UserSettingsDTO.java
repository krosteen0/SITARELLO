package it.uniroma3.siw.model;

import jakarta.validation.constraints.NotBlank;

public class UserSettingsDTO {

    @NotBlank(message = "Lo username è obbligatorio")
    private String username;

    // Getter e Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}