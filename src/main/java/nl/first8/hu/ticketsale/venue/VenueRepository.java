package nl.first8.hu.ticketsale.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
}
