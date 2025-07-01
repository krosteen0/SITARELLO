package it.uniroma3.siw.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductFormDTO {
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "La categoria è obbligatoria")
    private String categoria;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String descrizione;

    @Positive(message = "Il prezzo deve essere positivo")
    private Double prezzo;

    private List<MultipartFile> images;

    // Getters and Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }
    public List<MultipartFile> getImages() { return images; }
    public void setImages(List<MultipartFile> images) { this.images = images; }
}