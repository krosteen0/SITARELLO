package it.uniroma3.siw.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductForm {
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "La categoria è obbligatoria")
    private String categoria;

    @Positive(message = "Il prezzo deve essere positivo")
    private Double prezzo;

    private String descrizione;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}