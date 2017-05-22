package nl.first8.hu.ticketsale.sales;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.registration.RegistrationRepository;
import nl.first8.hu.ticketsale.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import nl.first8.hu.ticketsale.venue.Concert;

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
    public void insertSale(Long accountId, Long ticketId, Integer price) {
        insertSale(accountId, ticketId, price, Date.from(Instant.now()));
    }

    protected void insertSale(Long accountId, Long concertId, Integer price, final Date timestamp) {
        Account account = registrationRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Unknown account Id " + accountId));
        Concert concert = venueRepository.findConcertById(concertId).orElseThrow(() -> new RuntimeException("Unknown concert Id " + concertId));

        Ticket ticket = new Ticket(concert, account);
        salesRepository.insert(ticket);

        Sale sale = new Sale();
        sale.setTicket(ticket);
        sale.setPrice(price);
        sale.setSellDate(timestamp);

        salesRepository.insert(sale);
    }

    public Optional<Sale> getSale(Long accountId, Long concertId) {
        Account account = registrationRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Unknown account Id " + accountId));
        Concert concert = venueRepository.findConcertById(concertId).orElseThrow(() -> new RuntimeException("Unknown concert Id " + concertId));

        return salesRepository.findTicket(new TicketId(concert, account))
                .flatMap(ticket -> salesRepository.findSaleByTicket(ticket));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void insertTicket(Long accountId, Long concertId) {
        Optional<Account> optAccount = registrationRepository.findById(accountId);
        Optional<Concert> optConcert = venueRepository.findConcertById(concertId);
        if (optAccount.isPresent() && optConcert.isPresent()) {
            Ticket ticket = new Ticket(optConcert.get(), optAccount.get());
            salesRepository.insert(ticket);
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
