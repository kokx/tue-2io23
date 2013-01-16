package ogo.spec.game;

import ogo.spec.game.model.Tile;
import ogo.spec.game.model.TileType;

/**
 * Test class to test various methods.
 */
public class Test {

    private void run() {
        //testCreatureEatFood();
    }

    private void testTileIsAdjacent() {
        Tile tile_1 = new Tile(TileType.DEEP_WATER, 2, 2);
        Tile tile_2 = new Tile(TileType.DEEP_WATER, 3, 2);
        boolean testA = tile_1.isAdjacent(tile_2);
        Tile tile_3 = new Tile(TileType.DEEP_WATER, 3, 3);
        boolean testB = tile_1.isAdjacent(tile_3);
        Tile tile_4 = new Tile(TileType.DEEP_WATER, 2, 3);
        boolean testC = tile_1.isAdjacent(tile_4);
        Tile tile_5 = new Tile(TileType.DEEP_WATER, 4, 3);
        boolean testD = tile_1.isAdjacent(tile_5);
        Tile tile_6 = new Tile(TileType.DEEP_WATER, 4, 4);
        boolean testE = tile_1.isAdjacent(tile_6);
        Tile tile_7 = new Tile(TileType.DEEP_WATER, 4, 2);
        boolean testF = tile_1.isAdjacent(tile_7);
        System.out.println("Tile.isAdjacent test case (a): " + testA);
        System.out.println("Tile.isAdjacent test case (b): " + testB);
        System.out.println("Tile.isAdjacent test case (c): " + testC);
        System.out.println("Tile.isAdjacent test case (d): " + testD);
        System.out.println("Tile.isAdjacent test case (e): " + testE);
        System.out.println("Tile.isAdjacent test case (f): " + testF);
    }

    /*private void testCreatureEatFood() {
        // create an air creature
        AirCreature airCreature = new AirCreature();
        // set air creature's life points to 20
        airCreature.setLifeNoChange(20);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // set air creature's life points to 13
        airCreature.setLifeNoChange(13);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // set air creature's life points to 19
        airCreature.setLifeNoChange(19);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
    }*/

    public static void main(String args[]) {
        new Test().run();
    }
}
