package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Data
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
}
