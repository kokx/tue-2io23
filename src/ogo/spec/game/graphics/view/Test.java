/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.graphics.view;

/**
 *
 * @author s102877
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Timer t = new Timer(500);
        Thread thread = new Thread(t);
        thread.start();
        while (true) {
            System.out.println(t.getTime());
        }
    }
}
