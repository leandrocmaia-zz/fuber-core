package com.maia.fubercore.model;

import lombok.Builder;
import lombok.Value;

import javax.persistence.*;

@Entity
@Value
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    User user;
    Category category;
    @ManyToOne(cascade = CascadeType.ALL)
    Car car;
    Double lat;
    Double lon;
    Status status;

    public enum Category {
        UBER_X
    }

    public enum Status {
        ONLINE,
        OFFLINE
    }

}
