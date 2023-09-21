package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String description;

    @Column(nullable = false, name = "created_date")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @OneToMany(mappedBy = "request")
    private Set<Offer> proposals;
}
