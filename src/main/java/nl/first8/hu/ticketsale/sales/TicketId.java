package nl.first8.hu.ticketsale.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.venue.Concert;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketId implements Serializable{
    private Concert concert;
    private Account account;
}
