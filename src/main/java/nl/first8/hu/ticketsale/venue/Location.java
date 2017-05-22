package nl.first8.hu.ticketsale.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.registration.Account;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private Long id;
    private String name;
    private List<Concert> concerts;

}
