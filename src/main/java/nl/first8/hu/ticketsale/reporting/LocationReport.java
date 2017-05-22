package nl.first8.hu.ticketsale.reporting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationReport implements Serializable {

    private String genre;
    private List<String> concertLocations = new ArrayList<>();
    private List<String> accountLocations = new ArrayList<>();
}
