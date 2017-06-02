package nl.first8.hu.ticketsale.artistinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.first8.hu.ticketsale.venue.Concert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Sander on 29-5-2017.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="GENRE")
    private Genre genre;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "artist")
    private List<Concert> concerts;

    public Artist(String name){
        this.name = name;
    }
}
