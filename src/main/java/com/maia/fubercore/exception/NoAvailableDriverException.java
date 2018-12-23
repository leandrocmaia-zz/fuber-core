package com.maia.fubercore.exception;

import com.maia.fubercore.model.Ride;
import lombok.Getter;

public class NoAvailableDriverException extends Throwable {

    @Getter
    private Ride ride;

    public NoAvailableDriverException(String message, Ride ride) {
        super(message);
        this.ride = ride;
    }
}
