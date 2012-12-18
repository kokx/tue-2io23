package ogo.spec.game.graphics.view;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static java.lang.Math.*;

/**
 * Class containing some methods to draw glut-like objects.
 *
 * @author maikel
 */
public class GraphicalObjects {

    GL2 gl;

    public GraphicalObjects(GL2 gl) {
        this.gl = gl;
    }

    public void drawSolidCube() {
        gl.glBegin(GL_QUADS);
        cubeVertices();
        gl.glEnd();
    }

    public void drawWireCube() {
        gl.glBegin(GL_LINE_STRIP);
        gl.glEnd();
    }

    private void cubeVertices() {
        // Bottom plane.
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(1, 1, 0);
        gl.glVertex3f(1, 0, 0);

        // Back plane.
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 0, 1);
        gl.glVertex3f(1, 0, 1);
        gl.glVertex3f(1, 0, 0);

        // Upper plane.
        gl.glVertex3f(0, 0, 1);
        // TODO: maybe finish this some day.

    }

    public void drawCylinder() {
        //TODO: optimize
        int slices = 100;
        gl.glBegin(GL_QUADS);
        double angle1; // angle of ray from origin to current point with x axis
        double angle2;
        double[] angles = new double[slices+1];
        for (int i = 0; i <= slices; i++) {
            angles[i] = (i * 2 * PI / slices);
        }
        for (int i = 0; i < slices; i++) {
            //angle1 = (float) (i * 2 * PI / slices); // compute angle
            //angle2 = (float) ((i + 1) * 2 * PI / slices); // compute angle
            angle1 = angles[i];
            angle2 = angles[i+1];
            gl.glNormal3d(cos(angle1), sin(angle1), 0);
            gl.glVertex3d(cos(angle1), sin(angle1), 0);
            
            //gl.glNormal3d(cos(angle1), sin(angle1), 0);
            gl.glVertex3d(cos(angle1), sin(angle1), 1);
            
            //gl.glNormal3d(cos(angle2), sin(angle2), 0);
            gl.glVertex3d(cos(angle2), sin(angle2), 1);
            
            //gl.glNormal3d(cos(angle2), sin(angle2), 0);
            gl.glVertex3d(cos(angle2), sin(angle2), 0);
        }
        gl.glEnd();
    }

    public void drawCylinder(float r, float height) {
        gl.glPushMatrix();
        gl.glScaled(r, r, height);
        drawCylinder();
        gl.glPopMatrix();
    }
}
