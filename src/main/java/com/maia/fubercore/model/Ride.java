package com.maia.fubercore.model;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import javax.persistence.*;
import java.util.Date;

@Entity
@Value
@Builder
@Wither
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    Driver driver;
    @ManyToOne
    User user;
    Double originLat;
    Double originLon;
    Double destLat;
    Double destLon;
    Date created;
    Status status;

    public enum Status {
        REQUESTED,
        ACCEPTED,
        IN_PROGRESS,
        FINISHED,
        CANCELED
    }

}
