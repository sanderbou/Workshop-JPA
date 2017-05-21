package nl.first8.hu.ticketsale.registration;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final RegistrationRepository repository;

    @Autowired
    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void insert(@NonNull final Account account) {
        repository.insert(account);
    }

    /**
     * Updates the e-mail address of the Account identified by the given
     * <code>id</code>.
     *
     * @param id the id of the Account to update
     * @param emailAddress the email address to update
     * @return the updated Account that reflects the change made by this
     * operation
     */
    public Account updateEmailAddress(final long id, @NonNull final String emailAddress) {
        final Account accountToUpdate = repository.findById(id).orElseThrow(() -> new RuntimeException("No Account exists with id " + id));
        accountToUpdate.setEmailAddress(emailAddress);

        return repository.update(accountToUpdate);
    }

    /**
     * Updates the AccountInfo of the Account identified by the given
     * <code>id</code>.
     *
     * @param id the id of the Account to update
     * @param info the new or updated AccountInfo
     * @return the updated Account that reflects the change made by this
     * operation
     */
    public Account updateInfo(final long id, @NonNull final AccountInfo info) {
        final Account accountToUpdate = repository.findById(id).orElseThrow(() -> new RuntimeException("No Account exists with id " + id));
        accountToUpdate.setInfo(info);

        return repository.update(accountToUpdate);
    }

    public Optional<Account> getById(final long id) {
        return repository.findById(id);
    }

    public Optional<Account> getByEmailAddress(@NonNull final String emailAddress) {
        return repository.findByEmailAddress(emailAddress);
    }

    List<Account> list() {
        return repository.findAll();
    }
}
