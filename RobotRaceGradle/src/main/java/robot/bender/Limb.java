/*
 * Course: Computer Graphics
 * Course Code: 2IV60
 * Assignment: RobotRace
 * Students: Arjan Boschman & Robke Geenen
 */
package robot.bender;

import bodies.Body;
import bodies.BufferManager;
import bodies.SingletonDrawable;
import bodies.StackBuilder;
import com.jogamp.opengl.util.gl2.GLUT;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import javax.media.opengl.GL2;
import robot.RobotBody;

/**
 * Convenience class used by {@link Bender} to draw the arms, legs, hands and
 * feet.
 *
 * @author Arjan Boschman
 * @author Robke Geenen
 */
public class Limb implements SingletonDrawable {

    /**
     * The number of cylinders to use for each limb.
     */
    public static final int RING_COUNT = 6;
    /**
     * The number of fingers to use on each hand.
     */
    private static final int FINGER_COUNT = 3;
    private static final float FINGER_OFFCENTER = 0.03F;

    public static final float HEIGHT_OUTER_SEGMENT = 0.5F / RING_COUNT;
    private static final float HEIGHT_INNER_SEGMENT = HEIGHT_OUTER_SEGMENT * 1.35F;

    public static final float HEIGHT_FOOT = 0.1F;
    private static final float HEIGHT_HAND = 0.07F;
    private static final float HEIGHT_FINGER = 0.0625F;

    private static final float RADIUS_OUTER_SEGMENT = 0.04F;
    private static final float RADIUS_INNER_SEGMENT = RADIUS_OUTER_SEGMENT * 0.8F;
    private static final float RADIUS_FOOT = (float)Math.sqrt(Math.pow(HEIGHT_FOOT, 2d) + Math.pow(RADIUS_OUTER_SEGMENT, 2d));
    private static final float RADIUS_HAND = 0.06F;
    private static final float RADIUS_FINGER = HEIGHT_FINGER - 0.05F;

    /**
     * The number of edges to give the rings of the various shapes.
     */
    private static final int SLICE_COUNT = 50;
    /**
     * The number of rings to use when calculating a partial torus curve.
     */
    private static final int STACK_COUNT = 20;

    private Body outerSegmentBody;
    private Body innerSegmentBody;
    private Body footBody;
    private Body handBody;
    private Body fingerBody;

    @Override
    public void initialize(GL2 gl, BufferManager.Initialiser bmInitialiser) {
        outerSegmentBody = new StackBuilder(bmInitialiser)
                .setSliceCount(SLICE_COUNT)
                .addConicalFrustum(RADIUS_OUTER_SEGMENT, RADIUS_OUTER_SEGMENT, 0F, HEIGHT_OUTER_SEGMENT, false, false)
                .build();
        innerSegmentBody = new StackBuilder(bmInitialiser)
                .setSliceCount(SLICE_COUNT)
                .addConicalFrustum(RADIUS_INNER_SEGMENT, RADIUS_INNER_SEGMENT, 0F, HEIGHT_INNER_SEGMENT, false, false)
                .build();
        footBody = new StackBuilder(bmInitialiser)
                .setSliceCount(SLICE_COUNT)
                .addPartialTorus(STACK_COUNT, RADIUS_FOOT, 0F, 0F, HEIGHT_FOOT, true, false)
                .build();
        handBody = new StackBuilder(bmInitialiser)
                .setSliceCount(SLICE_COUNT)
                .addConicalFrustum(RADIUS_OUTER_SEGMENT, RADIUS_HAND, 0F, HEIGHT_HAND, true, true)
                .build();
        fingerBody = new StackBuilder(bmInitialiser)
                .setSliceCount(SLICE_COUNT)
                .addConicalFrustum(RADIUS_FINGER, RADIUS_FINGER, 0F, HEIGHT_FINGER, false, false)
                .addPartialTorus(STACK_COUNT, RADIUS_FINGER, 0F, HEIGHT_FINGER, HEIGHT_FINGER + RADIUS_FINGER, false, false)
                .build();
    }

    public void drawSegment(GL2 gl, GLUT glut, boolean stickFigure) {
        gl.glPushMatrix();
        if (stickFigure) {
            gl.glTranslated(0d, 0d, HEIGHT_OUTER_SEGMENT / 2);
            gl.glScaled(RobotBody.STICK_THICKNESS, RobotBody.STICK_THICKNESS, HEIGHT_OUTER_SEGMENT);
            glut.glutSolidCube(1f);
        } else {
            outerSegmentBody.draw(gl);
            final double heightDifference = HEIGHT_OUTER_SEGMENT - HEIGHT_INNER_SEGMENT;
            gl.glTranslated(0d, 0d, heightDifference / 2);
            innerSegmentBody.draw(gl);
        }
        gl.glPopMatrix();
    }

    public void drawHand(GL2 gl, boolean stickFigure) {
        handBody.draw(gl);
        gl.glTranslated(0d, 0d, HEIGHT_HAND);
        for (int j = 0; j < FINGER_COUNT; j++) {
            gl.glPushMatrix();
            gl.glTranslated(FINGER_OFFCENTER * cos(toRadians(j * 360 / FINGER_COUNT)), FINGER_OFFCENTER * sin(toRadians(j * 360 / FINGER_COUNT)), 0d);
            fingerBody.draw(gl);
            gl.glPopMatrix();
        }
    }

    public void drawFoot(GL2 gl, boolean stickFigure) {
        gl.glPushMatrix();
        {
            gl.glTranslated(0, 0, HEIGHT_FOOT);
            gl.glRotated(180, 1, 0, 0);
            footBody.draw(gl);
        }
        gl.glPopMatrix();
    }

}
