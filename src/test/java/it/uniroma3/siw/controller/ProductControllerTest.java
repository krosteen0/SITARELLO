package it.uniroma3.siw.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.repository.CategoryRepository;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.UsersRepository;
import it.uniroma3.siw.service.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;
    
    @Mock
    private ProductImageRepository productImageRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UsersRepository usersRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private Model model;
    
    private ProductController productController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productController = new ProductController();
        // Use reflection to set mocked dependencies
        setField(productController, "productService", productService);
        setField(productController, "productImageRepository", productImageRepository);
        setField(productController, "productRepository", productRepository);
        setField(productController, "usersRepository", usersRepository);
        setField(productController, "categoryRepository", categoryRepository);
    }
    
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // Handle reflection errors
            System.err.println("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }
    
    @Test
    void testHandleModernProductCreation_MissingName() {
        // Test case for missing name
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            null, 10.0, "Description", "1", "New", images, model
        );
        
        assertEquals("{\"error\": \"Il nome del prodotto è obbligatorio\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_InvalidPrice() {
        // Test case for invalid price
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            "Test Product", 0.0, "Description", "1", "New", images, model
        );
        
        assertEquals("{\"error\": \"Il prezzo deve essere maggiore di zero\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_MissingDescription() {
        // Test case for missing description
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, null, "1", "New", images, model
        );
        
        assertEquals("{\"error\": \"La descrizione è obbligatoria\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_MissingCategory() {
        // Test case for missing category
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", null, "New", images, model
        );
        
        assertEquals("{\"error\": \"La categoria è obbligatoria\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_InvalidCategoryFormat() {
        // Test case for invalid category format
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", "invalid", "New", images, model
        );
        
        assertEquals("{\"error\": \"Categoria non valida\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_CategoryNotFound() {
        // Test case for category not found
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        
        List<MultipartFile> images = Arrays.asList(new MockMultipartFile("test", "test.jpg", "image/jpeg", new byte[100]));
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", "1", "New", images, model
        );
        
        assertEquals("{\"error\": \"Categoria non trovata\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_MissingImages() {
        // Test case for missing images
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", "1", "New", null, model
        );
        
        assertEquals("{\"error\": \"Carica almeno un'immagine\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_TooManyImages() {
        // Test case for too many images
        List<MultipartFile> manyImages = Arrays.asList(
            new MockMultipartFile("test1", "test1.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test2", "test2.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test3", "test3.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test4", "test4.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test5", "test5.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test6", "test6.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test7", "test7.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test8", "test8.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test9", "test9.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test10", "test10.jpg", "image/jpeg", new byte[100]),
            new MockMultipartFile("test11", "test11.jpg", "image/jpeg", new byte[100])
        );
        
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", "1", "New", 
            manyImages, model
        );
        
        assertEquals("{\"error\": \"Massimo 10 immagini consentite\"}", result);
    }
    
    @Test
    void testHandleModernProductCreation_InvalidImageType() {
        // Test case for invalid image type
        MockMultipartFile invalidImage = new MockMultipartFile("test", "test.txt", "text/plain", new byte[100]);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category("Test Category", "icon")));
        
        List<MultipartFile> images = Arrays.asList(invalidImage);
        String result = productController.handleModernProductCreation(
            "Test Product", 10.0, "Description", "1", "New", images, model
        );
        
        assertTrue(result.contains("Formato immagine non supportato"));
    }
}