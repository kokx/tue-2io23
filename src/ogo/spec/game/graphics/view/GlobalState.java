package ogo.spec.game.graphics.view;


/**
 * The state variables that describe how the
 * scene should be rendered.
 */
public class GlobalState {

    // State variables.
    public int w;               // Width of window in pixels.
    public int h;               // Height of window in pixels.

    public Vector cnt;          // Center point.
    public float vDist;         // Distance eye point to center point.
    public float vWidth;        // Width of scene to be shown.
    public float phi;           // Azimuth angle in radians.
    public float theta;         // Elevation angle in radians.

    /**
     * Default settings.
     */
    public final void init() {

        cnt = Vector.O;
        vDist = 60f;
        vWidth = 20;
        phi = (float) ((float) 1.25 * Math.PI);
        theta = 0.25f * Base.THETA_MAX;
    }

    public GlobalState() {
        init();
    }

    /**
     * Textual format.
     */
    @Override
    public String toString() {
        return "GlobalState{" +
                ", w=" + w +
                ", h=" + h +
                ", cnt=" + cnt +
                ", vDist=" + vDist +
                ", vWidth=" + vWidth +
                ", phi=" + phi +
                ", theta=" + theta +
                '}';
    }

}
