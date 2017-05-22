package nl.first8.hu.ticketsale.util;

import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.registration.AccountInfo;
import nl.first8.hu.ticketsale.sales.Ticket;
import nl.first8.hu.ticketsale.sales.TicketId;
import nl.first8.hu.ticketsale.venue.Concert;
import nl.first8.hu.ticketsale.venue.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
public class TestRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Account createDefaultAccount(String emailAdress) {
        AccountInfo info = new AccountInfo("TestStraat", "0612345678");
        Account account = new Account(emailAdress);
        account.setInfo(info);
        entityManager.persist(info);
        entityManager.persist(account);
        return account;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Ticket createDefaultTicket(Account acc, String artist, String location) {
        Account account = entityManager.find(Account.class, acc.getId());
        Concert concert = createDefaultConcert(artist, location);
        Ticket ticket = new Ticket(concert, account);
        entityManager.persist(ticket);
        return ticket;
    }

    public Ticket findTicket(Concert concert, Account account) {
        TicketId key = new TicketId(concert, account);
        return entityManager.find(Ticket.class, key);
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Concert createDefaultConcert(String artist, String locationName) {
        Location location = createLocation(locationName);
        Concert concert = new Concert();
        concert.setArtist(artist);
        concert.setGenre("Grindcore");
        concert.setLocation(location);
        entityManager.persist(concert);
        return concert;

    }

    private Location createLocation(String locationName) {
        Location location = new Location();
        location.setName(locationName);
        entityManager.persist(location);
        return location;
    }

    
}
