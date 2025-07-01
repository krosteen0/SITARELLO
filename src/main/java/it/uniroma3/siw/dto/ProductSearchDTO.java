package it.uniroma3.siw.dto;

public class ProductSearchDTO {
    private String searchTerm;
    private String categoria;
    private Double prezzoMin;
    private Double prezzoMax;
    private String sortBy = "id"; // Default sort by id (newest first)
    
    public ProductSearchDTO() {}
    
    public ProductSearchDTO(String searchTerm, String categoria, Double prezzoMin, Double prezzoMax, String sortBy) {
        this.searchTerm = searchTerm;
        this.categoria = categoria;
        this.prezzoMin = prezzoMin;
        this.prezzoMax = prezzoMax;
        this.sortBy = sortBy != null ? sortBy : "id";
    }
    
    // Getters and Setters
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Double getPrezzoMin() {
        return prezzoMin;
    }
    
    public void setPrezzoMin(Double prezzoMin) {
        this.prezzoMin = prezzoMin;
    }
    
    public Double getPrezzoMax() {
        return prezzoMax;
    }
    
    public void setPrezzoMax(Double prezzoMax) {
        this.prezzoMax = prezzoMax;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "id";
    }
    
    // Metodi di utilità
    public boolean hasSearchTerm() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
    
    public boolean hasCategoria() {
        return categoria != null && !categoria.trim().isEmpty();
    }
    
    public boolean hasPrezzoMin() {
        return prezzoMin != null && prezzoMin > 0;
    }
    
    public boolean hasPrezzoMax() {
        return prezzoMax != null && prezzoMax > 0;
    }
    
    public boolean hasFilters() {
        return hasSearchTerm() || hasCategoria() || hasPrezzoMin() || hasPrezzoMax();
    }
    
    @Override
    public String toString() {
        return "ProductSearchDTO{" +
                "searchTerm='" + searchTerm + '\'' +
                ", categoria='" + categoria + '\'' +
                ", prezzoMin=" + prezzoMin +
                ", prezzoMax=" + prezzoMax +
                ", sortBy='" + sortBy + '\'' +
                '}';
    }
}
