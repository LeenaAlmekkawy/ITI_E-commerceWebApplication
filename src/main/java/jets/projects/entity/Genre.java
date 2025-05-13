package jets.projects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Genre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre 
{

    @Id
    @Column(name = "name", length = 100)
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "genre")
    private List<UserInterest> userInterests = new ArrayList<>();
}