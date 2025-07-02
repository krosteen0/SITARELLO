package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String name;

    @NotNull(message = "La categoria è obbligatoria")
    @ManyToOne
    private Category category;

    @Positive(message = "Il prezzo deve essere positivo")
    private Double price;

    @ManyToOne
    private Users seller;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;
    
    @Transient
    private String base64Image;

    public Product() {
        this.images = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Users getSeller() { return seller; }
    public void setSeller(Users seller) { this.seller = seller; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images != null ? images : new ArrayList<>(); }
    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings != null ? ratings : new ArrayList<>(); }
    
    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
    
    // Metodi utility per i rating
    public Double getAverageRating() {
        if (this.ratings == null || this.ratings.isEmpty()) {
            return 0.0;
        }
        return this.ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }
    
    public Integer getRatingCount() {
        return this.ratings != null ? this.ratings.size() : 0;
    }
}