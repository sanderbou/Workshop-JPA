package nl.first8.hu.ticketsale.sales;

import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.registration.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final RegistrationRepository registrationRepository;
    private final SalesRepository salesRepository;

    @Autowired
    public SalesService(RegistrationRepository registrationRepository, SalesRepository salesRepository) {
        this.registrationRepository = registrationRepository;
        this.salesRepository = salesRepository;
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Ticket insert(Long accountId, Ticket ticket) {
        Optional<Account> optAccount = registrationRepository.findById(accountId);
        if (optAccount.isPresent()) {
            ticket.setAccount(optAccount.get());
            salesRepository.insert(ticket);
            return ticket;
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
