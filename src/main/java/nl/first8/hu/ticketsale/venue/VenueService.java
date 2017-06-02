package nl.first8.hu.ticketsale.venue;

import nl.first8.hu.ticketsale.artistinfo.Genre;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Sander on 2-6-2017.
 */
@Service
public class VenueService {

    private VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository){
        this.venueRepository=venueRepository;
    }

    public List<Concert> searchByArtistname(String artistname) {
        return venueRepository.searchByArtistname(artistname);
    }
    public List<Concert> searchByArtistgenre(String artistgenre) {
        return venueRepository.searchByArtistgenre(artistgenre);
    }
    public List<Concert> searchByLocationConcert(String locationconcert) {
        return venueRepository.searchByLocationConcert(locationconcert);
    }
}
