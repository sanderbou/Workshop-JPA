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

    public Account update(@NonNull final Account account) {
        return repository.update(account);
    }

    public Optional<Account> getById(@NonNull final Long id) {
        return repository.findById(id);
    }

    public Optional<Account> getByEmailAddress(@NonNull final String emailAddress) {
        return repository.findByEmailAddress(emailAddress);
    }

    List<Account> list() {
        return repository.findAll();
    }
}
