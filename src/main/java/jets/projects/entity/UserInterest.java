package jets.projects.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserInterest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserInterest 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_ID")
    private Long interestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre", nullable = false)
    private Genre genre;
}