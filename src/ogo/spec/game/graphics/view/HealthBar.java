package ogo.spec.game.graphics.view;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;

/**
 *
 * @author maikel
 */
public class HealthBar {

    /**
     * Draws a health bar in XOY plane.
     *
     * @param gl gl object to draw on
     * @param percentage percentage of the health bar that is filled (in [0,1]).
     * @param height height of the health bar (length is 1).
     */
    public static void draw(GL2 gl, double percentage, double height) {
        /*float[] material;
        material = GUI.Materials.GREEN_PLASTIC;
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, material, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, material, 4);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, material, 8);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SHININESS, material, 12);*/
        
        gl.glDisable(GL_LIGHTING);
        gl.glColor3f(0, 1, 0);
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0, 0, 1);
        gl.glVertex2d(0, height);
        gl.glVertex2d(percentage, height);
        gl.glVertex2d(percentage, 0);
        gl.glVertex2d(0, 0);
        gl.glEnd();

        /*material = GUI.Materials.RED_PLASTIC;
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, material, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, material, 4);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, material, 8);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SHININESS, material, 12);*/
gl.glColor3f(1, 0, 0);
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(0, 0, 1);
        gl.glVertex2d(percentage, height);
        gl.glVertex2d(1, height);
        gl.glVertex2d(1, 0);
        gl.glVertex2d(percentage, 0);
        gl.glEnd();
        
        gl.glEnable(GL_LIGHTING);
    }
}
