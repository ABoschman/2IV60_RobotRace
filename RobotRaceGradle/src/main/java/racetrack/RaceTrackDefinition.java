package racetrack;

import robotrace.Vector;

public class RaceTrackDefinition {

    public static final int RTD_TEST = 0;

    public static Vector getTrackPoint(double t, RaceTrack raceTrack) {
        switch (raceTrack.getTrackType()) {
            case RTD_TEST:
                return new Vector(10 * Math.cos(2d * Math.PI * t), 14 * Math.sin(2d * Math.PI * t), -5 * Math.cos(2d * Math.PI * t));
            default:
                return Vector.O;
        }
    }

    private static Vector getTrackNormalLen(double t, RaceTrack raceTrack) {
        final Vector previous = getTrackPoint(t - (1d / RaceTrack.SLICE_COUNT), raceTrack);
        final Vector original = getTrackPoint(t, raceTrack);
        final Vector next = getTrackPoint(t + (1d / RaceTrack.SLICE_COUNT), raceTrack);
        final Vector lower = new Vector(original.x(), original.y(), original.z() - RaceTrack.TRACK_HEIGHT);
        final Vector normal1 = lower.subtract(original).cross(next.subtract(original));
        final Vector normal2 = previous.subtract(original).cross(lower.subtract(original));
        return normal1.add(normal2);
    }

    public static Vector getTrackNormal(double t, RaceTrack raceTrack) {
        return getTrackNormalLen(t, raceTrack).normalized();
    }

    private static Vector getTrackTangentLen(double t, RaceTrack raceTrack) {
        final Vector previous = getTrackPoint(t - (1d / RaceTrack.SLICE_COUNT), raceTrack);
        final Vector original = getTrackPoint(t, raceTrack);
        final Vector next = getTrackPoint(t + (1d / RaceTrack.SLICE_COUNT), raceTrack);
        final Vector delta1 = next.subtract(original);
        final Vector delta2 = original.subtract(previous);
        final Vector deltaTotal = delta1.add(delta2);
        return new Vector(deltaTotal.x(), deltaTotal.y(), 0);
    }

    public static Vector getTrackTangent(double t, RaceTrack raceTrack) {
        return getTrackTangentLen(t, raceTrack).normalized();
    }

    public static Vector getLanePoint(double t, RaceTrack raceTrack, int laneNumber) {
        final Vector translate = getTrackNormal(t, raceTrack).scale((laneNumber - (RaceTrack.LANE_COUNT / 2d) + 0.5d) * RaceTrack.LANE_WIDTH);
        return getTrackPoint(t, raceTrack).add(translate);
    }

    public static Vector getLaneNormal(double t, RaceTrack raceTrack, int laneNumber) {
        return getTrackNormal(t, raceTrack);
    }

    public static Vector getLaneTangent(double t, RaceTrack raceTrack, int laneNumber) {
        return getTrackTangent(t, raceTrack);
    }
}
