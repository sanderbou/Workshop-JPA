package nl.first8.hu.ticketsale.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.venue.Concert;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TicketId.class)
public class Ticket implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name="concert_id", referencedColumnName = "id")
    private Concert concert;

    @Id
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

}
