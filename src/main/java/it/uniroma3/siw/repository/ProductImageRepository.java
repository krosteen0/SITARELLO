package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}