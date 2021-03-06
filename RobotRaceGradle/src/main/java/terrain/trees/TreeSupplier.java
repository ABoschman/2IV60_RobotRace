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
package terrain.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import robotrace.Vector;
import terrain.HeightMap;

/**
 * Can be used to generate trees. This class is NOT actually responsible for
 * constructing the trees themselves, but rather for choosing a proper location
 * to grow a tree.
 *
 * Valid tree locations must meet the following requirements:
 *
 * 1: They're within the bounds of the terrain. 2: They're outside the bounds of
 * any optionally declarable forbidden zones. 3: They're not too close to
 * previously generated trees. 4: They're not underwater. 5: They're on
 * reasonably flat terrain.
 *
 * @author Arjan Boschman
 */
public class TreeSupplier implements Supplier<Tree> {

    /**
     * Random seed that will be used every time to ensure that the trees don't
     * actually change from one run of the program to the next. This number has
     * been more or less arbitrarily chosen.
     */
    private static final long RAND_SEED = 95_873_649_098L;
    /**
     * Every tree demands a clearing, a circle with this radius around itself.
     * Within these bounds, other trees are prevented from growing.
     */
    private static final int TREE_CLEARING_RADIUS = 5;

    /**
     * The maximum elevation difference in meters that can occur in a 1 meter
     * radius around a spot in order for that spot to be considered level enough
     * to support tree growth.
     */
    private static final float MAX_DECLINATION = 0.5f;

    /**
     * The maximum elevation on which to place trees.
     */
    private static final float MAX_TREE_ELEVATION = 145F;

    private static final int MAX_NR_UNIQUE_TREE_MODELS = 5;

    private final Random rand = new Random(RAND_SEED);
    private final TreeGenerator treeGenerator = new TreeGenerator();
    private final List<Tree.Node> cachedTreeTrunks = new ArrayList<>();
    private final Set<Rectangle> forbiddenAreas = new HashSet<>();
    private final Rectangle bounds;
    private final HeightMap heightMap;
    private final Foliage foliage;

    public TreeSupplier(Rectangle bounds, HeightMap heightMap, Foliage foliage) {
        this.bounds = bounds;
        this.heightMap = heightMap;
        this.foliage = foliage;
    }

    /**
     * Adds a forbidden area to this TreeGenerator. No more trees will
     * henceforth be allowed to spawn within these bounds. This does not affect
     * trees that have already been generated.
     *
     * @param x      The x-coordinate in meters of the bounding box.
     * @param y      The y-coordinate in meters of the bounding box.
     * @param width  The width in meters of the bounding box.
     * @param height The height in meters of the bounding box.
     */
    public void addForbiddenArea(double x, double y, double width, double height) {
        this.forbiddenAreas.add(new Rectangle(x, y, width, height));
    }

    @Override
    public Tree get() {
        while (true) {
            final Point2D coords = new Point2D(
                    rand.nextDouble() * bounds.getWidth() + bounds.getX(),
                    rand.nextDouble() * bounds.getHeight() + bounds.getY());
            if (checkForbidden(coords)
                    || checkAreaIsTooSteep(coords)
                    || checkAreaIsUnderWater(coords)
                    || checkAreaIsAboveTreeLine(coords)) {
                continue;
            }
            addForbiddenArea(coords.getX() - TREE_CLEARING_RADIUS,
                    coords.getY() - TREE_CLEARING_RADIUS,
                    2f * TREE_CLEARING_RADIUS, 2f * TREE_CLEARING_RADIUS);
            return new Tree(foliage,
                    new Vector(coords.getX(), coords.getY(),
                            heightMap.heightAt(coords.getX(), coords.getY())), getTreeTrunk());
        }
    }

    /**
     * Check if this supplier has already generated the maximum allotted number
     * of tree types. If yes, then return a randomly selected trunk from the
     * cache. Otherwise, generate a new trunk and cache it before returning it.
     *
     * @return The root node of a tree.
     */
    private Tree.Node getTreeTrunk() {
        if (cachedTreeTrunks.size() < MAX_NR_UNIQUE_TREE_MODELS) {
            final Tree.Node trunk = treeGenerator.makeTreeTrunk();
            cachedTreeTrunks.add(trunk);
            return trunk;
        } else {
            return cachedTreeTrunks.get(rand.nextInt(MAX_NR_UNIQUE_TREE_MODELS));
        }
    }

    /**
     * Check if it is allowed to generate a tree at the given location.
     *
     * @param coords The point to check.
     * @return True if the given coordinates are inside one of the forbidden
     *         areas and are thus illegible.
     */
    private boolean checkForbidden(final Point2D coords) {
        return forbiddenAreas.stream().anyMatch((rect) -> rect.contains(coords));
    }

    /**
     * Check if it is allowed to generate a tree at the given location.
     *
     * @param coords The point to check.
     * @return True if the given coordinates are on a steep hill and are thus
     *         illegible.
     */
    private boolean checkAreaIsTooSteep(final Point2D coords) {
        final float center = heightMap.heightAt(coords.getX(), coords.getY());
        final float north = heightMap.heightAt(coords.getX(), coords.getY() - 1d);
        final float south = heightMap.heightAt(coords.getX(), coords.getY() + 1d);
        final float west = heightMap.heightAt(coords.getX() - 1d, coords.getY());
        final float east = heightMap.heightAt(coords.getX() + 1d, coords.getY());
        return Math.abs(center - north) > MAX_DECLINATION
                || Math.abs(center - south) > MAX_DECLINATION
                || Math.abs(center - west) > MAX_DECLINATION
                || Math.abs(center - east) > MAX_DECLINATION;
    }

    /**
     * Check if it is allowed to generate a tree at the given location.
     *
     * @param coords The point to check.
     * @return True if the given coordinates are underwater and are thus
     *         illegible.
     */
    private boolean checkAreaIsUnderWater(final Point2D coords) {
        return heightMap.heightAt(coords.getX(), coords.getY()) < 0f;
    }

    /**
     * Check if it is allowed to generate a tree at the given location.
     *
     * @param coords The point to check.
     * @return True if the given coordinates are above the tree line and are
     *         thus illegible.
     */
    private boolean checkAreaIsAboveTreeLine(final Point2D coords) {
        return heightMap.heightAt(coords.getX(), coords.getY()) > MAX_TREE_ELEVATION;
    }

}
