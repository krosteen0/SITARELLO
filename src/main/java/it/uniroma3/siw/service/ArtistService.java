package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    // Save an artist
    public Artist saveArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    // Find an artist by ID
    public Optional<Artist> findArtistById(Long id) {
        return artistRepository.findById(id);
    }

    // Delete an artist by ID
    public void deleteArtistById(Long id) {
        artistRepository.deleteById(id);
    }

    // Retrieve all artists
    public Iterable<Artist> findAllArtists() {
        return artistRepository.findAll();
    }
}
