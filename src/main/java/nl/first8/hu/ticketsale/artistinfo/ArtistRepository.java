package nl.first8.hu.ticketsale.artistinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Sander on 1-6-2017.
 */
@Repository
public class ArtistRepository {

    private final EntityManager entityManager;

    @Autowired
    public ArtistRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void insert(final Artist artist){
        entityManager.persist(artist);
    }

    public Artist update(Artist artist){
        return entityManager.merge(artist);
    }

    public Optional<Artist> findById(final Long id) {
        return Optional.ofNullable(entityManager.find(Artist.class, id));
    }

    Optional<Artist> findByName(String name) {
        try {
            return Optional.of(entityManager.createQuery("SELECT a FROM Artist a WHERE a.name =:name", Artist.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<Artist> findAll() {
        return entityManager.createQuery("SELECT a FROM Artist a", Artist.class).getResultList();
    }
}
