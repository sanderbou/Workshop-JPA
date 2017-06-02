package nl.first8.hu.ticketsale.venue;

import nl.first8.hu.ticketsale.artistinfo.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class VenueRepository {

    private final EntityManager entityManager;

    @Autowired
    public VenueRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Concert> findConcertById(Long concertId) {
        return Optional.ofNullable(entityManager.find(Concert.class, concertId));
    }

    public List<Concert> searchByArtistname(String artistname) {
        try {
            return entityManager.createQuery("SELECT c FROM Concert c WHERE c.artist.name =:artistname", Concert.class)
                    .setParameter("artistname", artistname)
                    .getResultList();
        } catch (NoResultException ex) {
            return Collections.emptyList();
        }
    }

    public List<Concert> searchByArtistgenre(String artistgenre) {
        try {
            return entityManager.createQuery("SELECT c FROM Concert c WHERE c.artist.genre =:artistgenre", Concert.class)
                    .setParameter("artistgenre", Genre.valueOf(artistgenre))
                    .getResultList();
        } catch (NoResultException ex) {
            return Collections.emptyList();
        }
    }

    public List<Concert> searchByLocationConcert(String locationconcert) {
        try {
            return entityManager.createQuery("SELECT c FROM Concert c WHERE c.location.name = :locationconcert", Concert.class)
                    .setParameter("locationconcert", locationconcert)
                    .getResultList();
        } catch (NoResultException ex) {
            return Collections.emptyList();
        }
    }
}
