package ogo.spec.game.graphics.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.media.opengl.GL2;

/**
 *
 * @author maikel
 */
public class Wavefront {

    List<int[]> faces;
    List<double[]> vertices;
    List<double[]> normals;
    List<double[]> texcoords;
    boolean useNormals = false;
    boolean useTexCoords = false;
    GL2 gl;

    public void readWavefront(String filename, GL2 gl) throws FileNotFoundException {
        this.gl = gl;
        File file = new File(filename);
        readWavefront(file);
    }

    public void readWavefront(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);
        vertices = new ArrayList<double[]>();
        faces = new ArrayList<int[]>();
        normals = new ArrayList<double[]>();
        texcoords = new ArrayList<double[]>();
        while (fileScanner.hasNextLine()) {
            readLine(fileScanner);
        }
    }

    public void readWavefront(InputStream input) {
        Scanner scanner = new Scanner(input);
        vertices = new ArrayList<double[]>();
        faces = new ArrayList<int[]>();
        normals = new ArrayList<double[]>();
        texcoords = new ArrayList<double[]>();
        while (scanner.hasNextLine()) {
            readLine(scanner);
        }
    }

    private void readLine(Scanner src) {
        String next = src.next();

        if (next.equals("#")) {
            readComment(src);
        } else if (next.equals("vt")) {
            readTextureCoordinate(src);
        } else if (next.equals("vn")) {
            readNormal(src);
        } else if (next.equals("vp")) {
            readSpaceVertex(src);
        } else if (next.equals("v")) {
            readVertex(src);
        } else if (next.equals("f")) {
            readFace(src);
        }
    }

    private void readComment(Scanner src) {
        src.nextLine();
    }

    private void readVertex(Scanner src) {
        String line = src.nextLine();
        line = line.substring(1);
        String[] numbers_string = line.split(" ");
        double[] numbers = new double[numbers_string.length];
        for (int i = 0; i < numbers_string.length; i++) {
            numbers[i] = Double.parseDouble(numbers_string[i]);
        }
        vertices.add(numbers);
    }

    private void readTextureCoordinate(Scanner src) {
        useTexCoords = false;
        String line = src.nextLine();
        line = line.substring(1);
        String[] numbers_string = line.split(" ");
        double[] numbers = new double[numbers_string.length];
        for (int i = 0; i < numbers_string.length; i++) {
            numbers[i] = Double.parseDouble(numbers_string[i]);
        }
        texcoords.add(numbers);
    }

    private void readNormal(Scanner src) {
        useNormals = true;
        String line = src.nextLine();
        line = line.substring(1);
        String[] numbers_string = line.split(" ");
        double[] numbers = new double[numbers_string.length];
        for (int i = 0; i < numbers_string.length; i++) {
            numbers[i] = Double.parseDouble(numbers_string[i]);
        }
        normals.add(numbers);
    }

    private void readSpaceVertex(Scanner src) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void readFace(Scanner src) {
        //TODO: read normals
        String line = src.nextLine();
        line = line.substring(1);
        String[] numbers_string = line.split(" ");
        int[] face = new int[numbers_string.length];
        for (int i = 0; i < numbers_string.length; i++) {
            String[] args = numbers_string[i].split("/");
            face[i] = Integer.parseInt(args[0]);
        }
        faces.add(face);
    }

    public void drawPoints() {
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (double[] vertex : vertices) {
            gl.glVertex3d(vertex[0], vertex[1], vertex[2]);
        }
        gl.glEnd();
    }

    public void drawTriangles() {
        normalize();
        gl.glBegin(GL2.GL_TRIANGLES);
        for (int[] face : faces) {
            for (int i = 0; i < face.length; i++) {
                if (useNormals) {
                    //N.B. untested code
                    double[] normal = normals.get(face[i] - 1);
                    gl.glNormal3dv(normal, 0);
                }
                if (useTexCoords) {
                    double[] texCoords = texcoords.get(i);
                    gl.glTexCoord3dv(texCoords, 0);
                }
                double[] vertex = vertices.get(face[i] - 1);
                gl.glVertex3dv(vertex, 0);
            }
        }
        gl.glEnd();

        // Draw normals.
        /*
         * for (int[] face : faces) { for (int i = 0; i < face.length; i++) {
         * double[] vertex = vertices.get(face[i] - 1);
         *
         * if (useNormals) { //N.B. untested code double[] normal =
         * normals.get(face[i] - 1); gl.glNormal3d(normal[0], normal[1],
         * normal[2]);
         *
         * gl.glBegin(GL2.GL_LINES); gl.glVertex3d(vertex[0], vertex[1],
         * vertex[2]); gl.glVertex3d(vertex[0] + normal[0], vertex[1] +
         * normal[1], vertex[2] + normal[2]); gl.glEnd(); } }
         }
         */
    }

    private void normalize() {
        double[] min = vertices.get(0).clone(), max = vertices.get(0).clone();
        for (double[] vertex : vertices) {
            for (int i = 0; i < vertex.length; i++) {
                if (vertex[i] > max[i]) {
                    max[i] = vertex[i];
                } else if (vertex[i] < min[i]) {
                    min[i] = vertex[i];
                }
            }
        }

        for (double[] vertex : vertices) {
            for (int i = 0; i < vertex.length; i++) {
                vertex[i] -= min[i];
                vertex[i] = vertex[i] / (max[i] - min[i]);
            }
        }
    }
}
