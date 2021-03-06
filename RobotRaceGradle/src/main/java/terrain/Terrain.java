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

import Texture.ImplementedTexture;
import bodies.Body;
import bodies.BufferManager;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.shape.Rectangle;
import static javax.media.opengl.GL.GL_CULL_FACE;
import javax.media.opengl.GL2;
import robotrace.Lighting;
import robotrace.Material;
import robotrace.Vector;
import terrain.trees.Foliage;
import terrain.trees.Tree;
import terrain.trees.TreeSupplier;

/**
 * Implementation of the terrain.
 *
 * @author Arjan Boschman
 */
public class Terrain {

    /**
     * The translation of the terrain relative to the world origin.
     */
    private static final Vector TERRAIN_LEVEL = new Vector(0d, 0d, -145d);
    /**
     * The translation of the water relative to the world origin.
     */
    private static final float WATER_LEVEL = 0f;
    private static final String TERRAIN_TEXTURE_FILENAME = "terrain.png";

    private final Set<Tree> trees = new HashSet<>();
    private Body terrainBody;
    private Body waterBody;

    public void initialize(GL2 gl, BufferManager.Initialiser bmInitialiser) {
        final Foliage foliage = new Foliage();
        foliage.initialize(gl, bmInitialiser);
        final FractalTerrainGenerator heightMap = FractalTerrainGenerator.create();
        final TreeSupplier treeSupplierClose = new TreeSupplier(new Rectangle(-150d, -150d, 300d, 300d), heightMap, foliage);
        treeSupplierClose.addForbiddenArea(-25d, -25d, 50d, 50d);
        final TreeSupplier treeSupplierFar = new TreeSupplier(new Rectangle(-500d, -500d, 1000d, 1000d), heightMap, foliage);
        treeSupplierFar.addForbiddenArea(-50d, -50d, 100d, 100d);
        for (int i = 0; i < 15; i++) {
            trees.add(treeSupplierClose.get());
            trees.add(treeSupplierFar.get());
        }
        final ImplementedTexture terrainTexture = new ImplementedTexture(gl, TERRAIN_TEXTURE_FILENAME, true, false);
        this.terrainBody = new TerrainFactory(1000f, 1000f, 1f)
                .makeTerrain(bmInitialiser, heightMap, terrainTexture);
        this.waterBody = new TerrainFactory(1000f, 1000f, 100f)
                .makeTerrain(bmInitialiser, (x, y) -> WATER_LEVEL, null);
    }

    /**
     * Draws the terrain.
     *
     * @param gl       The instance of GL2 responsible for drawing the body.
     * @param glut     An instance of GLUT that can be optionally used to assist
     *                 in drawing.
     * @param camPos   The position of the camera in world coordinates.
     * @param lighting The Lighting instance responsible for calculating the
     *                 lighting in this scene. Can be used to set the colours of
     *                 bodies before drawing them.
     */
    public void draw(GL2 gl, GLUT glut, Vector camPos, Lighting lighting) {
        gl.glPushMatrix();
        {
            gl.glTranslated(TERRAIN_LEVEL.x(), TERRAIN_LEVEL.y(), TERRAIN_LEVEL.z());
            gl.glEnable(GL_CULL_FACE);
            lighting.setMaterial(gl, Material.DIRT, true);
            terrainBody.draw(gl);
            lighting.setMaterial(gl, Material.WATER);
            waterBody.draw(gl);
            gl.glDisable(GL_CULL_FACE);
            final Vector camPosRelativeToTerrain = camPos.subtract(TERRAIN_LEVEL);
            trees.stream().forEach((tree) -> tree.draw(gl, camPosRelativeToTerrain, lighting));
        }
        gl.glPopMatrix();
    }

}
