package nl.first8.hu.ticketsale.artistinfo;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Sander on 1-6-2017.
 */
@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository){
        this.artistRepository=artistRepository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void insert(@NonNull final Artist artist) {
        artistRepository.insert(artist);
    }

}
