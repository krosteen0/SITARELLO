package it.uniroma3.siw.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    // Visualizza l'elenco degli artisti
    @GetMapping
    public String getAllArtists(Model model) {
        model.addAttribute("artists", artistService.findAllArtists());
        return "artists"; // Template per l'elenco degli artisti
    }

    // Visualizza il dettaglio di un artista
    @GetMapping("/{id}")
    public String getArtistById(@PathVariable Long id, Model model) {
        Optional<Artist> artist = artistService.findArtistById(id);
        if (artist.isPresent()) {
            model.addAttribute("artist", artist.get());
            return "artist"; // Template per il dettaglio dell'artista
        } else {
            return "artist-not-found"; // Template opzionale per artista non trovato
        }
    }

    // Create a new artist (form submission)
    @PostMapping
    public String createArtist(@ModelAttribute Artist artist) {
        artistService.saveArtist(artist);
        return "redirect:/artists";
    }

    // Delete an artist by ID
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistById(id);
        return "redirect:/artists";
    }
}
