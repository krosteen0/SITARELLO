package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.ProductImage;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
}