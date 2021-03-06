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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import robotrace.Vector;
import terrain.trees.Tree.Node;

/**
 * Responsible for procedurally generating tree shapes.
 *
 * @author Arjan Boschman
 */
public class TreeGenerator {

    /**
     * Random seed that will be used every time to ensure that the trees don't
     * actually change from one run of the program to the next. This number has
     * been more or less arbitrarily chosen.
     */
    private static final long RAND_SEED = 34_542_123_874L;

    /**
     * How many times the tree's branches can split off into child branches.
     */
    private static final int TREE_DEPTH = 4;
    /**
     * The maximum number of leafs that may ever be generated on the same
     * branch.
     */
    private static final int MAX_NR_LEAFS_ON_BRANCH = 12;
    /**
     * The maximum number of child branches that may ever be generated on one
     * branch.
     */
    private static final int MAX_NR_BRANCHES_ON_BRANCH = 8;
    /**
     * The minimum number of branches that must be on the trunk of a tree.
     */
    private static final int MIN_NR_BRANCHES_ON_BRANCH = 4;
    /**
     * The minimum scale for trunks. Scale in in the range (0 <= s <= 1) and
     * becomes smaller the deeper one descends into a tree's branches.
     */
    private static final float MIN_TRUNK_SCALE = 0.5f;

    /**
     * The maximum trunk length. Used along with the scale to position and scale
     * all branches.
     */
    private static final float MAX_TRUNK_LENGTH = 50f;
    /**
     * The maximum trunk radius. Used along with the scale to scale all
     * branches.
     */
    public static final float MAX_TRUNK_RADIUS = 2.5f;

    /**
     * The maximum rotation of a node around the Y-axis relative to its parent,
     * in degrees.
     */
    private static final float MAX_Y_ANGLE = 45f;
    /**
     * The maximum rotation of a node around the Z-axis relative to its parent,
     * in degrees.
     */
    private static final float MAX_Z_ANGLE = 360f;

    /**
     * The maximum rotation of a trunk node around the Y-axis;
     */
    private static final float MAX_TRUNK_ANGLE = 5f;

    private final Random rand = new Random(RAND_SEED);

    public Tree.Node makeTreeTrunk() {
        final float trunkYRotation = rand.nextFloat() * MAX_TRUNK_ANGLE;
        final float trunkZRotation = rand.nextFloat() * MAX_Z_ANGLE;
        return makeBranch(0, Math.max(MIN_TRUNK_SCALE, rand.nextFloat()), 0f, trunkZRotation, trunkYRotation);
    }

    /**
     * Recursively makes a new branch node and descendants.
     *
     * @param depth The generation number of this branch. The trunk is
     *              generation zero, direct descendants are generation one, etc.
     * @param scale Suggests the scale of this branch. Scale is dependant on the
     *              scale of the direct parent, and on this branches relative
     *              position on its parent branch. For example; a first
     *              generation branch that's located high on the trunk may be
     *              smaller than a second generation branch that's located
     *              lower. Scale ranges between 0 and 1 and goes down with every
     *              generation.
     * @return A new branch node, fully configured with descendants.
     */
    private Tree.Node makeBranch(int depth, float scale, float translation, float zRotation, float yRotation) {
        final Set<Node> childBranches = new HashSet<>();
        final Set<Node> childLeafs = new HashSet<>();
        final Vector scaleVector = new Vector(MAX_TRUNK_RADIUS * scale, MAX_TRUNK_RADIUS * scale, MAX_TRUNK_LENGTH * scale);
        final float branchLength = (float) scaleVector.z();
        if (depth < TREE_DEPTH) {
            final int nrChildBranches = getNrBranches(depth);
            final int nrChildren = nrChildBranches + getNrLeafs(depth);
            for (int i = 0; i < nrChildren; i++) {
                final float childYRotation = rand.nextFloat() * MAX_Y_ANGLE;
                final float childZRotation = rand.nextFloat() * MAX_Z_ANGLE;
                if (i < nrChildBranches) {
                    final float translationScaleFactor = rand.nextFloat() * 0.4f + 0.3f;
                    final float childTranslation = branchLength * translationScaleFactor;
                    final float childScale = scale * (1f - translationScaleFactor);
                    childBranches.add(makeBranch(depth + 1, childScale, childTranslation, childZRotation, childYRotation));
                } else {
                    final float translationScaleFactor = rand.nextFloat() * 0.4f + 0.6f;
                    final float childTranslation = branchLength * translationScaleFactor;
                    final float sidewaysTranslation = (float) scaleVector.x() * MAX_TRUNK_RADIUS * (1f - translationScaleFactor);
                    childLeafs.add(makeLeaf(childTranslation, childZRotation, childYRotation, sidewaysTranslation));
                }
            }
        }
        return new Tree.Node(translation, scaleVector, zRotation, yRotation, childLeafs, childBranches);
    }

    private int getNrLeafs(int depth) {
        return Math.min(depth * depth + 1, MAX_NR_LEAFS_ON_BRANCH);
    }

    private int getNrBranches(int depth) {
        return Math.max(rand.nextInt(MAX_NR_BRANCHES_ON_BRANCH), MIN_NR_BRANCHES_ON_BRANCH);
    }

    private Tree.Node makeLeaf(float translation, float zRotation, float yRotation, float sidewaysTranslation) {
        return new Tree.Node(translation, new Vector(3d, 3d, 1d), zRotation, yRotation, sidewaysTranslation);
    }

}
