package nl.first8.hu.ticketsale.sales;

import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.registration.RegistrationRepository;
import nl.first8.hu.ticketsale.venue.Concert;
import nl.first8.hu.ticketsale.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final RegistrationRepository registrationRepository;
    private final SalesRepository salesRepository;
    private final VenueRepository venueRepository;

    @Autowired
    public SalesService(RegistrationRepository registrationRepository, SalesRepository salesRepository, VenueRepository venueRepository) {
        this.registrationRepository = registrationRepository;
        this.salesRepository = salesRepository;
        this.venueRepository = venueRepository;
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void insert(Long accountId, Long concertId) {
        Optional<Account> optAccount = registrationRepository.findById(accountId);
        Optional<Concert> optConcert = venueRepository.findConcertById(concertId);
        if (optAccount.isPresent() && optConcert.isPresent()) {
            //TODO Create ticket for given account and concert
            throw new UnsupportedOperationException();
        } else {
            throw new RuntimeException("Unknown account Id " + accountId);
        }
    }

    public List<Ticket> getById(Long accountId) {
        Optional<Account> optAccount = registrationRepository.findById(accountId);
        if (optAccount.isPresent()) {
            return salesRepository.findByAccount(optAccount.get());
        } else {
            throw new RuntimeException("Unknown account Id " + accountId);
        }
    }
}
