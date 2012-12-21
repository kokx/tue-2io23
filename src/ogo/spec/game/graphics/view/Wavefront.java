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
    GL2 gl;

    public void readWavefront(String filename, GL2 gl) throws FileNotFoundException {
        this.gl = gl;
        File file = new File(filename);
        readWavefront(file);
    }

    public void readWavefront(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        while (fileScanner.hasNextLine()) {
            readLine(fileScanner);
        }
    }

    public void readWavefront(InputStream input) {
        Scanner scanner = new Scanner(input);
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        while (scanner.hasNextLine()) {
            readLine(scanner);
        }
    }

    private void readLine(Scanner src) {
        String next = src.next();
         switch (next) {
         case "#":
         readComment(src);
         break;
         case "vt":
         readTextureCoordinate(src);
         break;
         case "vn":
         readNormal(src);
         break;
         case "vp":
         readSpaceVertex(src);
         break;
         case "v":
         readVertex(src);
         break;
         case "f":
         readFace(src);
         break;
         default:
         //System.out.println(next);
         //throw new IllegalArgumentException();
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void readNormal(Scanner src) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void readSpaceVertex(Scanner src) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void readFace(Scanner src) {
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
        gl.glBegin(GL2.GL_TRIANGLES);
        for (int[] face : faces) {
            for (int i = 0; i < face.length; i++) {
                double[] vertex = vertices.get(face[i] - 1);
                gl.glVertex3d(vertex[0], vertex[1], vertex[2]);
            }

        }
        gl.glEnd();
    }

    public static void main(String[] args) {
        Wavefront w = new Wavefront();
        w.readWavefront(System.in);
    }
}
