package it.uniroma3.siw.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.repository.CategoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                    new Category("Elettronica", "fas fa-laptop"),
                    new Category("Abbigliamento", "fas fa-tshirt"),
                    new Category("Libri", "fas fa-book-open"),
                    new Category("Musica", "fas fa-music"),
                    new Category("Fotografia", "fas fa-camera"),
                    new Category("Sport", "fas fa-futbol"),
                    new Category("Casa e Giardino", "fas fa-home"),
                    new Category("Giocattoli", "fas fa-gamepad")
            );
            categoryRepository.saveAll(categories);
        }
    }
}
