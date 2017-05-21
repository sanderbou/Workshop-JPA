package nl.first8.hu.ticketsale.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.registration.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    private Long id;
    private String artist;
    private String genre;
    private String location;
    private Account account;

    public Ticket(String artist, String genre, String location) {
        this.artist = artist;
        this.genre = genre;
        this.location = location;
    }
}
