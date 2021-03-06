/*
 * TU/e Eindhoven University of Technology
 * Course: Computer Graphics
 * Course Code: 2IV60
 * Assignment: RobotRace
 * 
 * This code is based on 6 template classes, as well as the RobotRaceLibrary. 
 * Both were provided by the course tutor, currently prof.dr.ir. 
 * J.J. (Jack) van Wijk. (e-mail: j.j.v.wijk@tue.nl)
 * 
 * Copyright (C) 2015 Arjan Boschman, Robke Geenen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package racetrack;

import Texture.ImplementedTexture;
import java.util.Arrays;
import java.util.List;
import javax.media.opengl.GL2;
import robotrace.Vector;

/**
 * 
 * @author Robke Geenen
 */
public class RaceTrackDefinition {

    public static final int RTD_TEST = 0;
    public static final int RTD_TEST_ELEVATED = 1;
    public static final int RTD_TEST_BEZ = 2;
    public static final int RTD_O = 3;
    public static final int RTD_L = 4;
    public static final int RTD_C = 5;
    public static final int RTD_CUSTOM = 6;

    private static final int RTD_MAX_TYPE = 6;

    protected static int getMaxTypeID() {
        return RTD_MAX_TYPE;
    }

    private static final List<CubicBezierPath> BEZIER_TEST = Arrays.asList(
            new CubicBezierPath(Vector.Z, Vector.Z.add(Vector.X.scale(10d)), Vector.Z.add(Vector.Y.scale(20d)).add(Vector.X.scale(10d)), Vector.Z.add(Vector.Y.scale(20d))),
            new CubicBezierPath(Vector.Z.add(Vector.Y.scale(20d)), Vector.Z.add(Vector.Y.scale(20d)).add(Vector.X.scale(-10d)), Vector.Z.add(Vector.X.scale(-10d)), Vector.Z)
    );
    private static final List<CubicBezierPath> BEZIER_O = Arrays.asList(
            new CubicBezierPath(new Vector(0d, -17d, 1d), new Vector(19d, -17d, 1d), new Vector(19d, 17d, 1d), new Vector(0d, 17d, 1d)),
            new CubicBezierPath(new Vector(0d, 17d, 1d), new Vector(-19d, 17d, 1d), new Vector(-19d, -17d, 1d), new Vector(0d, -17d, 1d))
    );
    private static final List<CubicBezierPath> BEZIER_L = Arrays.asList(
            new CubicBezierPath(new Vector(7d, -16d, 1d), new Vector(12d, -16d, 1d), new Vector(12d, -9d, 1d), new Vector(7d, -9d, 1d)),
            new CubicBezierPath(new Vector(7d, -9d, 1d), new Vector(7d, -9d, 1d), new Vector(2d, -9d, 1d), new Vector(2d, -9d, 1d)),
            new CubicBezierPath(new Vector(2d, -9d, 1d), new Vector(-3d, -9d, 1d), new Vector(-3d, -9d, 1d), new Vector(-3d, -4d, 1d)),
            new CubicBezierPath(new Vector(-3d, -4d, 1d), new Vector(-3d, -4d, 1d), new Vector(-3d, 13d, 1d), new Vector(-3d, 13d, 1d)),
            new CubicBezierPath(new Vector(-3d, 13d, 1d), new Vector(-3d, 18d, 1d), new Vector(-10d, 18d, 1d), new Vector(-10d, 13d, 1d)),
            new CubicBezierPath(new Vector(-10d, 13d, 1d), new Vector(-10d, 13d, 1d), new Vector(-10d, -11d, 1d), new Vector(-10d, -11d, 1d)),
            new CubicBezierPath(new Vector(-10d, -11d, 1d), new Vector(-10d, -16d, 1d), new Vector(-10d, -16d, 1d), new Vector(-5d, -16d, 1d)),
            new CubicBezierPath(new Vector(-5d, -16d, 1d), new Vector(-5d, -16d, 1d), new Vector(7d, -16d, 1d), new Vector(7d, -16d, 1d))
    );
    private static final List<CubicBezierPath> BEZIER_C = Arrays.asList(
            new CubicBezierPath(new Vector(10d, -17d, 1d), new Vector(15d, -17d, 1d), new Vector(15d, -10d, 1d), new Vector(10d, -10d, 1d)),
            new CubicBezierPath(new Vector(10d, -10d, 1d), new Vector(-5d, -10d, 1d), new Vector(-5d, 10d, 1d), new Vector(10d, 10d, 1d)),
            new CubicBezierPath(new Vector(10d, 10d, 1d), new Vector(15d, 10d, 1d), new Vector(15d, 17d, 1d), new Vector(10d, 17d, 1d)),
            new CubicBezierPath(new Vector(10d, 17d, 1d), new Vector(-15d, 17d, 1d), new Vector(-15d, -17d, 1d), new Vector(10d, -17d, 1d))
    );
    private static final List<CubicBezierPath> BEZIER_CUSTOM = Arrays.asList(
            new CubicBezierPath(new Vector(-17d, 12d, 1d), new Vector(-17d, 5d, 1d), new Vector(-17d, -5d, 6d), new Vector(-17d, -12d, 6d)),
            new CubicBezierPath(new Vector(-17d, -12d, 6d), new Vector(-17d, -15d, 6d), new Vector(-15d, -17d, 6d), new Vector(-12d, -17d, 6d)),
            new CubicBezierPath(new Vector(-12d, -17d, 6d), new Vector(-5d, -17d, 6d), new Vector(6d, -17d, 11d), new Vector(13d, -17d, 11d)),
            new CubicBezierPath(new Vector(13d, -17d, 11d), new Vector(18d, -17d, 11d), new Vector(18d, -10d, 11d), new Vector(13d, -10d, 11d)),
            new CubicBezierPath(new Vector(13d, -10d, 11d), new Vector(6d, -10d, 11d), new Vector(-6d, -10d, 16d), new Vector(-13d, -10d, 16d)),
            new CubicBezierPath(new Vector(-13d, -10d, 16d), new Vector(-18d, -10d, 16d), new Vector(-18d, -3d, 16d), new Vector(-13d, -3d, 16d)),
            new CubicBezierPath(new Vector(-13d, -3d, 16d), new Vector(6d, -3d, 16d), new Vector(4d, 12d, 13.4d), new Vector(-4d, 12d, 12d)),
            new CubicBezierPath(new Vector(-4d, 12d, 12d), new Vector(-12d, 12d, 10.6d), new Vector(-13d, -3d, 8d), new Vector(6d, -3d, 8d)),
            new CubicBezierPath(new Vector(6d, -3d, 8d), new Vector(19d, -3d, 8d), new Vector(19d, 17d, 8d), new Vector(13d, 17d, 8d)),
            new CubicBezierPath(new Vector(13d, 17d, 8d), new Vector(10d, 17d, 8d), new Vector(6d, 17d, 1d), new Vector(2d, 17d, 1d)),
            new CubicBezierPath(new Vector(2d, 17d, 1d), new Vector(-1d, 17d, 1d), new Vector(-2d, 17d, 3d), new Vector(-5d, 17d, 3d)),
            new CubicBezierPath(new Vector(-5d, 17d, 3d), new Vector(-8d, 17d, 3d), new Vector(-9d, 17d, 1d), new Vector(-12d, 17d, 1d)),
            new CubicBezierPath(new Vector(-12d, 17d, 1d), new Vector(-15d, 17d, 1d), new Vector(-17d, 15d, 1d), new Vector(-17d, 12d, 1d))
    );

    protected static boolean getClosedTrack(int trackType) {
        switch (trackType) {
            case RTD_TEST:
                return true;
            case RTD_TEST_ELEVATED:
                return false;
            case RTD_TEST_BEZ:
                return true;
            case RTD_O:
                return true;
            case RTD_L:
                return true;
            case RTD_C:
                return true;
            case RTD_CUSTOM:
                return true;
            default:
                return false;
        }
    }

    protected static int getSliceCount(int trackType) {
        switch (trackType) {
            case RTD_TEST:
                return 50;
            case RTD_TEST_ELEVATED:
                return 50;
            case RTD_TEST_BEZ:
                return 50;
            case RTD_O:
                return 100;
            case RTD_L:
                return 100;
            case RTD_C:
                return 100;
            case RTD_CUSTOM:
                return 500;
            default:
                return 50;
        }
    }

    protected static ImplementedTexture getTopTexture(int trackType, GL2 gl) {
        switch (trackType) {
            case RTD_TEST:
            case RTD_TEST_ELEVATED:
            case RTD_TEST_BEZ:
            case RTD_O:
            case RTD_L:
            case RTD_C:
            case RTD_CUSTOM:
            default:
                return new ImplementedTexture(gl, "trackTop.png", false, false).setScale(1d, 20d).setStretch(true, false);
        }
    }

    protected static ImplementedTexture getBottomTexture(int trackType, GL2 gl) {
        switch (trackType) {
            case RTD_TEST:
            case RTD_TEST_ELEVATED:
            case RTD_TEST_BEZ:
            case RTD_O:
            case RTD_L:
            case RTD_C:
            case RTD_CUSTOM:
            default:
                return new ImplementedTexture(gl, "trackBottom.png", true, false).setScale(1d, 20d).setStretch(false, false);
        }
    }

    protected static ImplementedTexture getSideTexture(int trackType, GL2 gl) {
        switch (trackType) {
            case RTD_TEST:
            case RTD_TEST_ELEVATED:
            case RTD_TEST_BEZ:
            case RTD_O:
            case RTD_L:
            case RTD_C:
            case RTD_CUSTOM:
            default:
                return new ImplementedTexture(gl, "trackSide.jpg", true, true).setScale(0.2d).setStretch(false, false);
        }
    }

    protected static Vector getTrackPoint(int trackType, double t) {
        t = clip(t);
        switch (trackType) {
            case RTD_TEST:
                return new Vector(10d * Math.cos(2d * Math.PI * t), 14d * Math.sin(2d * Math.PI * t), 1d);
            case RTD_TEST_ELEVATED:
                return new Vector(10d * Math.cos(2d * Math.PI * t), 14d * Math.sin(2d * Math.PI * t), 6d - (5d * Math.cos(3d * Math.PI * t)));
            case RTD_TEST_BEZ:
                return calculateBezierPoint(t, BEZIER_TEST);
            case RTD_O:
                return calculateBezierPoint(t, BEZIER_O);
            case RTD_L:
                return calculateBezierPoint(t, BEZIER_L);
            case RTD_C:
                return calculateBezierPoint(t, BEZIER_C);
            case RTD_CUSTOM:
                return calculateBezierPoint(t, BEZIER_CUSTOM);
            default:
                return Vector.O;
        }
    }

    protected static Vector getTrackNormal(int trackType, double t) {
        t = clip(t);
        final double stepSize = 1d / getSliceCount(trackType);
        final Vector previous = getTrackPoint(trackType, t - stepSize);
        final Vector original = getTrackPoint(trackType, t);
        final Vector next = getTrackPoint(trackType, t + stepSize);
        final Vector lower = original.subtract(Vector.Z.normalized().scale(RaceTrack.TRACK_HEIGHT));
        final Vector normal1 = lower.subtract(original).cross(next.subtract(original));
        final Vector normal2 = previous.subtract(original).cross(lower.subtract(original));
        return normal1.add(normal2).normalized();
    }

    protected static Vector getTrackTangent(int trackType, double t) {
        t = clip(t);
        final double stepSize = 1d / getSliceCount(trackType);
        final Vector previous = getTrackPoint(trackType, t - stepSize);
        final Vector original = getTrackPoint(trackType, t);
        final Vector next = getTrackPoint(trackType, t + stepSize);
        final Vector delta1 = next.subtract(original);
        final Vector delta2 = original.subtract(previous);
        return delta1.add(delta2).normalized();
    }

    protected static Vector getLanePoint(int trackType, int laneNumber, double t) {
        t = clip(t);
        final Vector translate = getTrackNormal(trackType, t).scale((laneNumber - (RaceTrack.LANE_COUNT - 1) * 0.5d) * RaceTrack.LANE_WIDTH);
        return getTrackPoint(trackType, t).add(translate);
    }

    protected static Vector getLaneNormal(int trackType, int laneNumber, double t) {
        t = clip(t);
        return getTrackNormal(trackType, t);
    }

    protected static Vector getLaneTangent(int trackType, int laneNumber, double t) {
        t = clip(t);
        return getTrackTangent(trackType, t);
    }

    private static Vector calculateBezierPoint(double t, List<CubicBezierPath> bezierElements) {
        t = clip(t);
        final double tScaled = t * bezierElements.size();
        int iElement = (int) Math.floor(tScaled);
        double tElement = tScaled % 1d;
        if (iElement <= 0) {
            iElement = 0;
        }
        if (iElement >= bezierElements.size()) {
            iElement = bezierElements.size() - 1;
        }
        if (t == 1d) {
            iElement = bezierElements.size() - 1;
            tElement = 1d;
        }
        return bezierElements.get(iElement).calculatePointOnPath(tElement);
    }

    private static double clip(double t) {
        if (t == 1d) {
            return 1d;
        } else {
            return t % 1d;
        }
    }

    private static class CubicBezierPath {

        private final Vector p0;
        private final Vector p1;
        private final Vector p2;
        private final Vector p3;

        private CubicBezierPath(Vector p0, Vector p1, Vector p2, Vector p3) {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        private Vector calculatePointOnPath(double t) {
            t = clip(t);
            final Vector point = Vector.O
                    .add(p0.scale(Math.pow((1d - t), 3d)))
                    .add(p1.scale(3d * t * Math.pow((1d - t), 2d)))
                    .add(p2.scale(3d * (1d - t) * Math.pow(t, 2d)))
                    .add(p3.scale(Math.pow(t, 3d)));
            return point;
        }

    }

}
