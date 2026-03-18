package com.train.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String code;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    public Station() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getCity() { return city; }
    public String getState() { return state; }
}
