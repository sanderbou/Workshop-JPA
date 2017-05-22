package nl.first8.hu.ticketsale.registration;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationTestHelperService {

    private final EntityManager entityManager;

    @Autowired
    public RegistrationTestHelperService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Account createAccount(final String emailAddress, final String street, final String telephoneNumber, String city) {
        final Account account = new Account(null, emailAddress, new AccountInfo(street, telephoneNumber, city));

        entityManager.persist(account.getInfo());
        entityManager.persist(account);

        return account;
    }
}
