/*
 * Course: Computer Graphics
 * Course Code: 2IV60
 * Assignment: RobotRace
 * Students: Arjan Boschman & Robke Geenen
 */
package robot.bender;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import robot.Animation;

/**
 *
 * @author Arjan Boschman
 */
public class Leg {

    private static final float HIP_PERIOD_OFFSET = 1 / 12F;
    public static final int NR_HIP_JOINTS = 2;
    public static final int NR_KNEE_JOINTS = 3;
    public static final int NR_ANKLE_JOINTS = 1;

    private final Limb limb;
    private final float animationPeriodOffset;

    public Leg(Limb limb, float animationPeriodOffset) {
        this.limb = limb;
        this.animationPeriodOffset = animationPeriodOffset;
    }

    public void draw(GL2 gl, GLUT glut, boolean stickFigure, Animation animation) {
        gl.glPushMatrix();
        drawHips(gl, glut, stickFigure, animation);
        drawKnees(gl, glut, stickFigure, animation);
        drawAnkles(gl, glut, stickFigure, animation);
        limb.drawFoot(gl, glut, stickFigure);
        gl.glPopMatrix();
    }

    private void drawHips(GL2 gl, GLUT glut, boolean stickFigure, Animation animation) {
        final float hipFraction = animation.getLinearInterpolation(animationPeriodOffset + HIP_PERIOD_OFFSET);
        final float hipJointAngle = 26 - 48 - 48 * (float) Math.sin(hipFraction * 2 * Math.PI);
        for (int i = 0; i < NR_HIP_JOINTS; i++) {
            final float partialHipJointAngle = hipJointAngle / NR_HIP_JOINTS;
            gl.glRotated(partialHipJointAngle, -1, 0, 0);
            limb.drawSegment(gl, glut, stickFigure);
            gl.glTranslated(0, 0, Limb.HEIGHT_OUTER_SEGMENT);
        }
    }

    private void drawKnees(GL2 gl, GLUT glut, boolean stickFigure, Animation animation) {
        final float kneeFraction = animation.getLinearInterpolation(animationPeriodOffset);
        final float fractionInRadians = (float) (kneeFraction * 2 * Math.PI);
        final float kneeJointAngle = 25 + 36.6F + 12.2F * (float) ((Math.cos(fractionInRadians) * 4 + Math.cos(fractionInRadians*1) * 2));
        for (int i = 0; i < NR_KNEE_JOINTS; i++) {
            final float partialKneeJointAngle = kneeJointAngle / NR_KNEE_JOINTS;
            gl.glRotated(partialKneeJointAngle, -1, 0, 0);
            limb.drawSegment(gl, glut, stickFigure);
            gl.glTranslated(0, 0, Limb.HEIGHT_OUTER_SEGMENT);
        }
    }

    private void drawAnkles(GL2 gl, GLUT glut, boolean stickFigure, Animation animation) {
        final float ankleJointAngle = 0F;
        for (int i = 0; i < NR_ANKLE_JOINTS; i++) {
            final float partialAnkleJointAngle = ankleJointAngle / NR_ANKLE_JOINTS;
            gl.glRotated(partialAnkleJointAngle, -1, 0, 0);
            limb.drawSegment(gl, glut, stickFigure);
            gl.glTranslated(0, 0, Limb.HEIGHT_OUTER_SEGMENT);
        }
    }

}