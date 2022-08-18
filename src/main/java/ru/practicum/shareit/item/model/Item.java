package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @Column(name = "owner_id", updatable = false)
    private long owner;

    @Column(name = "request_id", updatable = false)
    private long request;

    @Transient
    private LastBooking lastBooking;

    @Transient
    private NextBooking nextBooking;

    @Transient
    private Set<Comment> comment = new HashSet<>();
}
