package nl.first8.hu.ticketsale.registration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountInfo {

    /*
    * TODO: implement the missing parts of this entity
     */
    private Long id;

    private String street;

    private String telephoneNumber;

    public AccountInfo(Long id, String street, String telephoneNumber) {
        this.id = id;
        this.street = street;
        this.telephoneNumber = telephoneNumber;
    }

    public AccountInfo(String street, String telephoneNumber) {
        this.street = street;
        this.telephoneNumber = telephoneNumber;
    }

}
