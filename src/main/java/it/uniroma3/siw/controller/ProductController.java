package it.uniroma3.siw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductForm;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.FileStorageService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/products/categoria")
    public String getProductsByCategoria(
            @RequestParam(required = false) String categoria,
            Model model) {
        List<Product> prodotti = productService.getProductsByCategoria(categoria);
        model.addAttribute("products", prodotti);
        model.addAttribute("categoria", categoria);
        model.addAttribute("redirectUrl", categoria != null ? "/products/categoria?categoria=" + categoria : "/products");
        return "products";
    }

    @GetMapping("/products")
    public String searchProducts(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Integer rating,
            Model model) {
        List<Product> filteredProducts = productService.filterProducts(categoria, price, rating);
        model.addAttribute("products", filteredProducts);
        StringBuilder redirectUrl = new StringBuilder("/products");
        boolean hasParams = false;
        if (categoria != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("categoria=").append(categoria);
            hasParams = true;
        }
        if (price != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("price=").append(price);
            hasParams = true;
        }
        if (rating != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("rating=").append(rating);
        }
        model.addAttribute("redirectUrl", redirectUrl.toString());
        return "products";
    }

    @GetMapping("/formNewProduct")
    public String showNewProductForm(Model model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Utente non loggato, redirect a /login");
            return "redirect:/login";
        }
        model.addAttribute("productForm", new ProductForm());
        logger.debug("Form per nuovo prodotto inizializzato");
        return "formNewProduct";
    }

    @PostMapping("/product")
    public String addProduct(
            @Valid @ModelAttribute("productForm") ProductForm productForm,
            BindingResult bindingResult,
            @RequestParam("images") List<MultipartFile> images,
            HttpSession session,
            Model model) {
        logger.debug("Inizio elaborazione POST /product");

        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Utente non loggato, redirect a /login");
            return "redirect:/login";
        }

        // Validate number of images
        long validImagesCount = images.stream().filter(image -> !image.isEmpty()).count();
        logger.debug("Numero di immagini valide: {}", validImagesCount);
        if (validImagesCount < 2 || validImagesCount > 10) {
            logger.warn("Numero di immagini non valido: {}", validImagesCount);
            model.addAttribute("errorMessage", "Devi caricare tra 2 e 10 immagini valide.");
            model.addAttribute("productForm", productForm);
            return "formNewProduct";
        }

        // Validate form fields
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder("Errori di validazione: ");
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            logger.warn(errors.toString());
            model.addAttribute("errorMessage", "Errore nei dati inseriti. Controlla i campi.");
            model.addAttribute("productForm", productForm);
            return "formNewProduct";
        }

        try {
            // Create new Product from ProductForm
            Product product = new Product();
            product.setNome(productForm.getNome());
            product.setCategoria(productForm.getCategoria());
            product.setPrezzo(productForm.getPrezzo());
            product.setDescrizione(productForm.getDescrizione());
            product.setAutore(loggedUser.getUsername());

            // Process uploaded images
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String filePath = fileStorageService.storeFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setFilePath(filePath);
                    productImage.setProduct(product);
                    product.getImages().add(productImage);
                }
            }

            productService.saveProduct(product);
            logger.info("Prodotto creato con successo: {}", product.getNome());
            return "redirect:/my-products";
        } catch (Exception e) {
            logger.error("Errore durante la creazione del prodotto", e);
            model.addAttribute("errorMessage", "Errore durante la creazione del prodotto: " + e.getMessage());
            model.addAttribute("productForm", productForm);
            return "formNewProduct";
        }
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product != null && product.getAutore().equals(loggedUser.getUsername())) {
            productService.deleteProduct(id);
        } else {
            model.addAttribute("errorMessage", "Non puoi rimuovere questo prodotto.");
        }
        return "redirect:/my-products";
    }

    @GetMapping("/my-products")
    public String getMyProducts(HttpSession session, Model model, HttpServletRequest request) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        List<Product> myProducts = productService.findProductsByAutore(loggedUser.getUsername());
        model.addAttribute("products", myProducts);
        model.addAttribute("redirectUrl", request.getRequestURI());
        return "myProducts";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model, @RequestParam(required = false) String redirectUrl) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products";
        }
        // Convert Product to ProductForm
        ProductForm productForm = new ProductForm();
        productForm.setNome(product.getNome());
        productForm.setCategoria(product.getCategoria());
        productForm.setPrezzo(product.getPrezzo());
        productForm.setDescrizione(product.getDescrizione());
        model.addAttribute("productForm", productForm);
        model.addAttribute("productId", id);
        model.addAttribute("redirectUrl", redirectUrl);
        return "editProduct";
    }

    @PostMapping("/product/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productForm") ProductForm productForm,
            BindingResult bindingResult,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(required = false) String redirectUrl,
            HttpSession session,
            Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Product existingProduct = productService.findById(id);
        if (existingProduct == null || !existingProduct.getAutore().equals(loggedUser.getUsername())) {
            return "redirect:/my-products";
        }

        // Validate number of images
        long validImagesCount = images.stream().filter(image -> !image.isEmpty()).count();
        if (validImagesCount + existingProduct.getImages().size() > 10) {
            logger.warn("Numero di immagini non valido: {} (totale con esistenti: {})", validImagesCount, validImagesCount + existingProduct.getImages().size());
            model.addAttribute("errorMessage", "Il numero totale di immagini non può superare 10.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }

        // Validate form fields
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder("Errori di validazione: ");
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            logger.warn(errors.toString());
            model.addAttribute("errorMessage", "Errore nei dati inseriti. Controlla i campi.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }

        try {
            // Update existing product
            existingProduct.setNome(productForm.getNome());
            existingProduct.setCategoria(productForm.getCategoria());
            existingProduct.setPrezzo(productForm.getPrezzo());
            existingProduct.setDescrizione(productForm.getDescrizione());

            // Process new images
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String filePath = fileStorageService.storeFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setFilePath(filePath);
                    productImage.setProduct(existingProduct);
                    existingProduct.getImages().add(productImage);
                }
            }

            productService.saveProduct(existingProduct);
            logger.info("Prodotto aggiornato con successo: {}", existingProduct.getNome());
            return (redirectUrl != null && !redirectUrl.isEmpty()) ? "redirect:" + redirectUrl : "redirect:/my-products";
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del prodotto", e);
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento del prodotto: " + e.getMessage());
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }
    }

    @GetMapping("/product/{id}")
    public String getProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String redirectUrl,
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }

        Users loggedUser = (Users) session.getAttribute("loggedUser");
        boolean hasRated = loggedUser != null && ratingService.hasUserRatedProduct(loggedUser, product);
        Double userRating = null;
        if (hasRated) {
            userRating = ratingService.getUserRating(loggedUser, product);
        }

        String finalRedirectUrl = redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : request.getHeader("Referer");
        if (finalRedirectUrl == null || finalRedirectUrl.contains("/product/" + id)) {
            finalRedirectUrl = "/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("hasRated", hasRated);
        model.addAttribute("userRating", userRating);
        model.addAttribute("redirectUrl", finalRedirectUrl);

        return "product";
    }
}