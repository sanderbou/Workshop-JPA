package nl.first8.hu.ticketsale.registration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class AccountInfo implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String street;

    private String telephoneNumber;

    private String city;

    public AccountInfo(Long id, String street, String telephoneNumber) {
        this.id = id;
        this.street = street;
        this.telephoneNumber = telephoneNumber;
    }

    public AccountInfo(String street, String telephoneNumber, String city) {
        this.street = street;
        this.telephoneNumber = telephoneNumber;
        this.city = city;
    }

}
