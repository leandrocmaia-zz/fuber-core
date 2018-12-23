package com.maia.fubercore.service;

import com.maia.fubercore.exception.NoAvailableDriverException;
import com.maia.fubercore.model.Car;
import com.maia.fubercore.model.Driver;
import com.maia.fubercore.model.Ride;
import com.maia.fubercore.model.User;
import com.maia.fubercore.repository.DriverRepository;
import com.maia.fubercore.repository.RideRepository;
import com.maia.fubercore.util.Triplet;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RideServiceTest {

    private RideService rideService;

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private DriverRepository driverRepository;

    private final Pair<Double, Double> locationBrandenburgGate = new Pair<>(52.516410, 13.378126);
    private final Pair<Double, Double> locationOstkreuz = new Pair<>(52.504393, 13.467889);

    @Before
    public void setUp() throws Exception {
        rideService = new RideService(rideRepository, driverRepository);
    }

    @Test
    public void should_create_models() {

        Driver driver = Driver
            .builder()
            .car(Car.builder().model("BMW").plate("PLATE 12345").build())
            .user(User.builder().name("Driver").build())
            .build();

        Ride ride = Ride.builder()
            .id(1l)
            .driver(driver)
            .user(User.builder().name("Customer").build())
            .originLat(locationOstkreuz.getKey())
            .originLon(locationOstkreuz.getValue())
            .destLat(locationBrandenburgGate.getKey())
            .destLon(locationBrandenburgGate.getValue()) // brandeburg tor
            .created(new Date())
            .status(Ride.Status.REQUESTED)
            .build();

        assertNotNull(ride);
    }

    @Test(expected = NoAvailableDriverException.class)
    public void should_fail_start_ride_no_drivers() throws NoAvailableDriverException {
        rideService.startRide(buildNewRide());
    }

    @Test
    public void should_assign_driver_to_ride() throws NoAvailableDriverException {

        // lat-long in order of distance from locationBrandenburgGate
        createDrivers(5, Arrays.asList(
            new Triplet<>(Driver.Status.ONLINE, 52.516410, 13.378126), //nearest
            new Triplet<>(Driver.Status.ONLINE, 52.519077, 13.403151),
            new Triplet<>(Driver.Status.ONLINE, 52.522715, 13.410314),
            new Triplet<>(Driver.Status.ONLINE, 52.533997, 13.420838),
            new Triplet<>(Driver.Status.ONLINE, 52.547887, 13.428647)
        ));
        Ride response = rideService.startRide(buildNewRide());

        assertNotNull(response.getDriver());
        assertThat(response.getDriver().getLat(), equalTo(52.516410));
        assertThat(response.getDriver().getLon(), equalTo(13.378126));
    }

    @Test
    public void should_assign_different_driver_to_ride() throws NoAvailableDriverException {

        // lat-long in order of distance from locationBrandenburgGate
        createDrivers(5, Arrays.asList(
            new Triplet<>(Driver.Status.OFFLINE, 52.516410, 13.378126),
            new Triplet<>(Driver.Status.OFFLINE, 52.519077, 13.403151),
            new Triplet<>(Driver.Status.ONLINE, 52.522715, 13.410314), // nearest
            new Triplet<>(Driver.Status.ONLINE, 52.533997, 13.420838),
            new Triplet<>(Driver.Status.ONLINE, 52.547887, 13.428647)
        ));
        Ride response = rideService.startRide(buildNewRide());

        assertNotNull(response.getDriver());
        assertThat(response.getDriver().getLat(), equalTo(52.522715));
        assertThat(response.getDriver().getLon(), equalTo(13.410314));
    }

    @Test
    public void should_wait_driver_to_confirm() {
        // https://eng.uber.com/tech-stack-part-one/
        // uber still uses polling and not message-driven events
        // i will probably do pub-sub pattern, needs some refactoring
    }

    private Ride buildNewRide() {
        return Ride
            .builder()
            .user(User.builder().name("Customer").build())
            .originLat(locationBrandenburgGate.getKey())
            .originLon(locationBrandenburgGate.getValue())
            .destLat(locationOstkreuz.getKey())
            .destLon(locationOstkreuz.getValue())
            .build();
    }

    private void createDrivers(int n, List<Triplet<Driver.Status, Double, Double>> driverMeta) {
        for (int i = 0; i < n; i++) {
            Driver driver = Driver
                .builder()
                .car(Car.builder().model("BMW").plate("PLATE 12345" + i).build())
                .user(User.builder().name("Driver" + 1).build())
                .lat(driverMeta.get(i).getMiddle())
                .lon(driverMeta.get(i).getRight())
                .status(driverMeta.get(i).getLeft())
                .build();
            driverRepository.save(driver);
        }
    }
}
