package com.maia.fubercore.util;

public class GeoUtils {

    public static Double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist =
            Math.sin(degreesToRadius(lat1))
            * Math.sin(degreesToRadius(lat2))
            + Math.cos(degreesToRadius(lat1))
            * Math.cos(degreesToRadius(lat2))
            * Math.cos(degreesToRadius(theta));
        dist = Math.acos(dist);
        dist = radiusToDegrees(dist);
        dist = dist * 60;
        dist = dist * 1852;
        return (dist);
    }

    private static double degreesToRadius(double degrees) {
        return (degrees * Math.PI / 180.0);
    }

    private static double radiusToDegrees(double radius) {
        return (radius * 180.0 / Math.PI);
    }

}
