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
package terrain;

import java.util.Random;

/**
 * Uses the diamond-square algorithm to generate realistic looking terrain.
 *
 * @author Arjan Boschman
 */
public class FractalTerrainGenerator implements HeightMap {

    /**
     * Random seed that will be used every time to ensure that the terrain
     * doesn't actually change from one run of the program to the next. This
     * number has been more or less arbitrarily chosen.
     */
    private static final long RAND_SEED = 12_345_678_422L;

    public static FractalTerrainGenerator create() {
        final FractalTerrainGenerator instance = new FractalTerrainGenerator(10, 0.3f);
        instance.initialise();
        return instance;
    }
    private final Random rand = new Random(RAND_SEED);
    private final int globalSize;
    private final int max;
    private final float[][] map;
    private final float roughness;

    /**
     * Make a new instance of the FractalTerrainGenerator. Note that before
     * using this object as a HeightMap, it needs to be initialised first.
     *
     * @param detail    A measure of the size of the cube of terrain that will
     *                  be generated. Size is obtained as follows: size = (2 ^
     *                  detail) + 1.
     * @param roughness A value between 0 and 1, this is a measure of the
     *                  smoothness of the terrain. Values near zero yield smooth
     *                  terrain, values near 1 yield mountainous terrain.
     * @see #create() Consider using this static factory method instead. It
     * creates the generator using some default values and also initialises it.
     */
    public FractalTerrainGenerator(int detail, float roughness) {
        this.globalSize = (int) Math.pow(2d, detail) + 1;
        this.max = globalSize - 1;
        this.map = new float[globalSize][globalSize];
        this.roughness = roughness;
    }

    /**
     * Must be called before this instance is used as a HeightMap. This method
     * will calculate the heights of all grid points in advance.
     */
    public void initialise() {
        final float initialValue = -50f;
        map[0][0] = initialValue;
        map[max][0] = initialValue;
        map[0][max] = initialValue;
        map[max][max] = initialValue;
        divide(max);
    }

    /**
     * Recursive method that divides the terrain into ever smaller surfaces and
     * performs the square-diamond steps on them.
     *
     * @param size The size to apply.
     */
    private void divide(int size) {
        final int half = size / 2;
        final float scale = roughness * size;
        if (half < 1) {
            return;
        }
        for (int y = half; y < max; y += size) {
            for (int x = half; x < max; x += size) {
                performDiamondStep(x, y, half, rand.nextFloat() * scale * 2f - scale);
            }
        }
        for (int y = 0; y <= max; y += half) {//Be mindful of rounding errors.
            for (int x = (y + half) % size; x <= max; x += size) {
                performSquareStep(x, y, half, rand.nextFloat() * scale * 2f - scale);
            }
        }
        divide(half);
    }

    /**
     * The square step. For each diamond in the array, set the midpoint of that
     * diamond to be the average of the four corner points plus a random value.
     *
     * @param x      Position on x-axis in the map array.
     * @param y      Position on y-axis in the map array.
     * @param radius Half the distance between the diamonds's opposing edges.
     * @param offset The z-axis offset to add to the average value. This is what
     *               adds randomness to the generation.
     */
    private void performSquareStep(int x, int y, int radius, float offset) {
        final float average
                = (map[x][Math.floorMod(y - radius, globalSize)] //Top
                + map[Math.floorMod(x - radius, globalSize)][y] //Left
                + map[x][Math.floorMod(y + radius, globalSize)] //Bottom
                + map[Math.floorMod(x + radius, globalSize)][y]) / 4f;  //Right
        map[x][y] = average + offset;
    }

    /**
     * The diamond step. For a certain square in the array, set the midpoint of
     * that square to be the average of the four corner points plus a random
     * value.
     *
     * @param x      Position on x-axis in the map array.
     * @param y      Position on y-axis in the map array.
     * @param radius Half the length of the square's edges.
     * @param offset The z-axis offset to add to the average value. This is what
     *               adds randomness to the generation.
     */
    private void performDiamondStep(int x, int y, int radius, float offset) {
        final float average
                = (map[Math.floorMod(x - radius, globalSize)][Math.floorMod(y - radius, globalSize)] //Top-Left
                + map[Math.floorMod(x - radius, globalSize)][Math.floorMod(y + radius, globalSize)] //Bottom-Left
                + map[Math.floorMod(x + radius, globalSize)][Math.floorMod(y - radius, globalSize)] //Top-Right
                + map[Math.floorMod(x + radius, globalSize)][Math.floorMod(y + radius, globalSize)]) / 4f; //Bottom-Right
        map[x][y] = average + offset;
    }

    @Override
    public float heightAt(double x, double y) {
        final int gridX = (int) x + globalSize / 2;
        final int gridY = (int) y + globalSize / 2;
        return map[Math.floorMod(gridX, globalSize)][Math.floorMod(gridY, globalSize)];
    }

}
