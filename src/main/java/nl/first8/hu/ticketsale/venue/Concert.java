package nl.first8.hu.ticketsale.venue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concert {

    private Long id;
    private String artist;
    private String genre;
    private Location location;

}
