package com.maia.fubercore.service;

import com.maia.fubercore.exception.NoAvailableDriverException;
import com.maia.fubercore.model.Driver;
import com.maia.fubercore.model.Ride;
import com.maia.fubercore.repository.DriverRepository;
import com.maia.fubercore.repository.RideRepository;
import com.maia.fubercore.util.GeoUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class RideService {

    private RideRepository rideRepository;
    private DriverRepository driverRepository;

    public RideService(RideRepository rideRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
    }

    public Ride startRide(Ride startRide) throws NoAvailableDriverException {
        Driver driver = this.findNearestDriver(startRide);

        return rideRepository.save(
            startRide
                .withStatus(Ride.Status.REQUESTED)
                .withDriver(driver)
                .withCreated(new Date())
        );

    }

    private Driver findNearestDriver(Ride ride) throws NoAvailableDriverException {
        List<Driver> onlineDrivers = driverRepository.findAllByStatus(Driver.Status.ONLINE);
        List<Pair<Driver, Double>> nearestDrivers = onlineDrivers
            .stream()
            .flatMap(driver -> Stream.of(new Pair<>(driver, findDistance(ride, driver))))
            .sorted(Comparator.comparing(Pair::getValue))
            .collect(toList());

        if (nearestDrivers.isEmpty())
            throw new NoAvailableDriverException("No available driver at the moment.", ride);

        return nearestDrivers.get(0).getKey();
    }

    private Double findDistance(Ride ride, Driver driver) {
        return GeoUtils.distance(
                            ride.getOriginLat(),
                            ride.getOriginLon(),
                            driver.getLat(),
                            driver.getLon()
        );
    }


}
