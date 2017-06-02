package nl.first8.hu.ticketsale.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Sander on 2-6-2017.
 */
@RestController
@RequestMapping("/venue")
@Transactional
public class VenueResource {
    private final VenueService venueService;

    @Autowired
    public VenueResource(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping(path = "/artistname/{artistname}")
    public List<Concert> searchByArtistname(@PathVariable("artistname") final String artistname) {
        return venueService.searchByArtistname(artistname);
    }
    @GetMapping(path = "/artistgenre/{artistgenre}")
    public List<Concert> searchByArtistgenre(@PathVariable("artistgenre") final String artistgenre) {
        return venueService.searchByArtistgenre(artistgenre);
    }
    @GetMapping(path = "/locationconcert/{locationconcert}")
    public List<Concert> searchByLocationConcert(@PathVariable("locationconcert") final String locationconcert) {
        return venueService.searchByLocationConcert(locationconcert);
    }
}

