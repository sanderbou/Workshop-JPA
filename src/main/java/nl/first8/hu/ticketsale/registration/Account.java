package nl.first8.hu.ticketsale.registration;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.sales.Ticket;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String emailAddress;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private AccountInfo info;

    @OneToMany(mappedBy = "account")
    private List<Ticket> tickets = new ArrayList<>();

    public Account(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Account(final Long id, final String emailAddress, final AccountInfo info) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.info = info;
    }

    public Account(Long id, String emailAddress) {
        this.id = id;
        this.emailAddress = emailAddress;
    }
}
