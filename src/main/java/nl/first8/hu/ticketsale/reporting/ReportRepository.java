package nl.first8.hu.ticketsale.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ReportRepository {

    private final EntityManager entityManager;

    @Autowired
    public ReportRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public LocationReport findGenreLocations(String genre) {
        //TODO Build report query
        throw new UnsupportedOperationException();
    }
}
