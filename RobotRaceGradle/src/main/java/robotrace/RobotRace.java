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
package robotrace;

import Camera.Camera;
import bodies.BufferManager;
import java.util.Arrays;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_FILL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import racetrack.RaceTrack;
import racetrack.RaceTrackDefinition;
import racetrack.RaceTrackFactory;
import robot.Robot;
import robot.RobotFactory;
import terrain.Terrain;

/**
 * Handles all of the RobotRace graphics functionality, which should be extended
 * per the assignment.
 *
 * OpenGL functionality: - Basic commands are called via the gl object; -
 * Utility commands are called via the glu and glut objects;
 *
 * GlobalState: The gs object contains the GlobalState as described in the
 * assignment: - The camera viewpoint angles, phi and theta, are changed
 * interactively by holding the left mouse button and dragging; - The camera
 * view width, vWidth, is changed interactively by holding the right mouse
 * button and dragging upwards or downwards; - The center point can be moved up
 * and down by pressing the 'q' and 'z' keys, forwards and backwards with the
 * 'w' and 's' keys, and left and right with the 'a' and 'd' keys; - Other
 * settings are changed via the menus at the top of the screen.
 *
 * Textures: Place your "track.jpg", "brick.jpg", "head.jpg", and "torso.jpg"
 * files in the same folder as this file. These will then be loaded as the
 * texture objects track, bricks, head, and torso respectively. Be aware, these
 * objects are already defined and cannot be used for other purposes. The
 * texture objects can be used as follows:
 *
 * gl.glColor3f(1f, 1f, 1f); track.bind(gl); gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0); gl.glVertex3d(0, 0, 0); gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0); gl.glTexCoord2d(1, 1); gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1); gl.glVertex3d(0, 1, 0); gl.glEnd();
 *
 * Note that it is hard or impossible to texture objects drawn with GLUT. Either
 * define the primitives of the object yourself (as seen above) or add
 * additional textured primitives to the GLUT object.
 * 
 * @author Arjan Boschman
 * @author Robke Geenen
 */
public class RobotRace extends Base {

    private static final int DEFAULT_STACKS = 25;
    private static final int DEFAULT_SLICES = 25;
    private static final int NUMBER_ROBOTS = 4;

    /**
     * Main program execution body, delegates to an instance of the RobotRace
     * implementation.
     *
     * @param args
     */
    public static void main(String args[]) {
        final RobotRace robotRace = new RobotRace();
        robotRace.run();
    }

    private final BufferManager bodyManager = new BufferManager();
    private final Camera camera = new Camera();
    private final Terrain terrain = new Terrain();
    private final RobotFactory robotFactory = new RobotFactory();
    private final RaceTrackFactory raceTrackFactory = new RaceTrackFactory();
    private final Lighting lighting = new Lighting();
    private final Robot[] robots;
    private final RaceTrack[] raceTracks;

    private double tPrevious = 0d;

    /**
     * Constructs this robot race by initializing robots, camera, track, and
     * terrain.
     */
    public RobotRace() {
        this.robots = new Robot[NUMBER_ROBOTS];
        this.raceTracks = new RaceTrack[5];
        setupObjects();
    }

    private void setupObjects() {

        robots[0] = robotFactory.makeBender(0, Material.GOLD);
        robots[1] = robotFactory.makeBender(1, Material.SILVER);
        robots[2] = robotFactory.makeBender(2, Material.WOOD);
        robots[3] = robotFactory.makeBender(3, Material.PLASTIC_ORANGE);

        robots[0].setSpeed(3d);
        robots[1].setSpeed(3d);
        robots[2].setSpeed(3d);
        robots[3].setSpeed(3d);

        robots[0].setLaneNumber(0);
        robots[1].setLaneNumber(1);
        robots[2].setLaneNumber(2);
        robots[3].setLaneNumber(3);

        raceTracks[0] = raceTrackFactory.makeRaceTrack(RaceTrackDefinition.RTD_TEST);
        raceTracks[1] = raceTrackFactory.makeRaceTrack(RaceTrackDefinition.RTD_O);
        raceTracks[2] = raceTrackFactory.makeRaceTrack(RaceTrackDefinition.RTD_L);
        raceTracks[3] = raceTrackFactory.makeRaceTrack(RaceTrackDefinition.RTD_C);
        raceTracks[4] = raceTrackFactory.makeRaceTrack(RaceTrackDefinition.RTD_CUSTOM);

    }

    /**
     * Called upon the start of the application. Primarily used to configure
     * OpenGL.
     */
    @Override
    public void initialize() {
        final BufferManager.Initialiser bmInitialiser = bodyManager.makeInitialiser(gl);
        lighting.initialize(gl, gs);

        // Enable blending.
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Normalize normals.
        gl.glEnable(GL_NORMALIZE);

        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        raceTrackFactory.initialize(gl, bmInitialiser);

        //Initialize factories and terrain.
        robotFactory.initialize(gl, bmInitialiser);

        terrain.initialize(gl, bmInitialiser);
        camera.initialize(gs, Arrays.asList(robots));

        bmInitialiser.finish();
    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        camera.update(gl, glu, gs, Arrays.asList(robots));
        lighting.setView(gl);
    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        final RaceTrack raceTrack = raceTracks[gs.trackNr];
        bodyManager.startDraw(gl);
        lighting.drawScene(gl);

        // Background color.
        gl.glClearColor((135f / 255f), (206f / 255f), (250f / 255f), 0f);
        // Clear background and depth buffer.
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }

        //Draw the robots.
        for (Robot robot : robots) {
            robot.update(raceTrack, gs.tAnim - tPrevious);
            robot.draw(gl, glut, gs.showStick, gs.tAnim, lighting);
        }

        // Draw the race track.
        raceTrack.draw(gl, lighting);

        // Draw the terrain.
        terrain.draw(gl, glut, camera.getCamPos(), lighting);

        //End the drawing and finish up.
        bodyManager.endDraw(gl);
        gl.glFlush();
        tPrevious = gs.tAnim;
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame() {
        //The radius of the sphere that sits at the origin.
        final float originSphereRadius = 0.05f;
        //Sets the color to yellow.
        lighting.setColor(gl, 1f, 1f, 0f, 1f);
        //Draw a yellow sphere at the origin.
        glut.glutSolidSphere(originSphereRadius, DEFAULT_SLICES, DEFAULT_STACKS);

        //Draw beams and cones along the x, y and z axes, respectively.
        drawAxis(1f, 0f, 0f);
        drawAxis(0f, 1f, 0f);
        drawAxis(0f, 0f, 1f);

        //Reset the color back to black.
        gl.glColor3f(0f, 0f, 0f);
    }

    private void drawAxis(float x, float y, float z) {
        //The width of the axis beams' short edges.
        final float axisThickness = 0.01f;
        //The height of the cone topping the axis.
        final float coneHeight = 0.1f;
        //The length of axis beams' long edge.
        final float axisLength = 1f - coneHeight;
        //Sets the color relative to the axis being drawn. (x=red,y=green,z=blue)
        lighting.setColor(gl, x, y, z, 1f);

        //Store the current matrix.
        gl.glPushMatrix();
        //Translate the cone to the correct position, depending on the axis being drawn.
        gl.glTranslatef(x * axisLength, y * axisLength, z * axisLength);
        /**
         * Rotate the cone so that it points along the axis. The cone is rotated
         * around the axis perpendicular to the one being drawn. The z-cone is
         * already rotated correctly, so it is not rotated.
         */
        gl.glRotatef(z == 1f ? 0f : 90f, -y, x, 0f);
        //Draw the cone, make base five times wider than the axis beam.
        glut.glutSolidCone(axisThickness * 5f, coneHeight, DEFAULT_SLICES, DEFAULT_STACKS);
        //Restore the original matrix.
        gl.glPopMatrix();

        //Store the current matrix.
        gl.glPushMatrix();
        //Translate the beam half its length into the direction of its axis.
        gl.glTranslatef(axisLength * 0.5f * x,
                axisLength * 0.5f * y,
                axisLength * 0.5f * z);
        //Stretch the beam along its axis to make it fit the previously defined axisLength.
        gl.glScalef(x == 0f ? 1f : axisLength / axisThickness,
                y == 0f ? 1f : axisLength / axisThickness,
                z == 0f ? 1f : axisLength / axisThickness);
        //Draw the beam.
        glut.glutSolidCube(axisThickness);
        //Restore the original matrix.
        gl.glPopMatrix();
    }

}
