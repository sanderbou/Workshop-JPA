package nl.first8.hu.ticketsale.registration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Account implements Serializable {

    /*
     * TODO: synchronize the insert and update of the Account instances with the associated AccountInfo
     *
     * (hint: the associated methods for persisting and updating can be simplified by removing a few lines of code)
     */

    @Id
    @GeneratedValue
    private Long id;

    private String emailAddress;

    @OneToOne
    private AccountInfo info;

    public Account(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Account(final Long id, final String emailAddress, final AccountInfo info) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.info = info;
    }

}
