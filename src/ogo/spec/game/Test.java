package ogo.spec.game;

import ogo.spec.game.model.AirCreature;
import ogo.spec.game.model.Tile;
import ogo.spec.game.model.TileType;

/**
 * Test class to test various methods.
 */
public class Test {

    private void run() {
        //testAirCreatureCanMove();
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
        airCreature.setLife(20);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // set air creature's life points to 13
        airCreature.setLife(13);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // set air creature's life points to 19
        airCreature.setLife(19);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // eat food
        airCreature.doEat(new Food(0));
        System.out.println("AirCreature's life points: " + airCreature.getLife());
    }*/

    /*private void testCreatureEatCreature() {
        // create an air creature
        AirCreature airCreature = new AirCreature();
        airCreature.setLife(15);
        // create a land creature
        LandCreature landCreature = new LandCreature();
        // print eat value for a land creature
        System.out.println("LandCreature's eat value for an AirCreature: " + airCreature.getEatValue(landCreature));
        // let the air creature eat the land creature
        airCreature.eatCreature(landCreature);
        System.out.println("AirCreature's life points: " + airCreature.getLife());

        // create a land creature
        LandCreature landCreatureTwo = new LandCreature();
        landCreatureTwo.setLife(20);
        // create an air creature
        AirCreature airCreatureTwo = new AirCreature();
        // print eat value for an air creature
        System.out.println("AirCreature's eat value for a LandCreature: " + landCreatureTwo.getEatValue(airCreatureTwo));
        // let the land creature eat the air creature
        landCreatureTwo.eatCreature(airCreatureTwo);
        System.out.println("LandcreatureTwo's life points: " + landCreatureTwo.getLife());

        // create an air creature
        AirCreature airCreatureThree = new AirCreature();
        airCreatureThree.setLife(2);
        // create a land creature
        LandCreature landCreatureThree = new LandCreature();
        // print eat value for a land creature
        System.out.println("LandCreature's eat value for an AirCreature: " + airCreatureThree.getEatValue(landCreatureThree));
        // let the air creature eat the land creature
        airCreatureThree.eatCreature(landCreatureThree);
        System.out.println("AirCreatureThree's life points: " + airCreatureThree.getLife());
    }*/

    /*private void testCreatureDealDamage() {
        // create an air creature
        AirCreature airCreature = new AirCreature();

        // set air creature's life points to 15
        airCreature.setLife(15);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // deal 2 damage to the air creature
        airCreature.dealDamage(2);
        System.out.println("AirCreature's life points: " + airCreature.getLife());

        // set air creature's life points to 1
        airCreature.setLife(1);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // deal 2 damage to the air creature
        airCreature.dealDamage(2);
        System.out.println("AirCreature's life points: " + airCreature.getLife());

        // set air creature's life points to 1
        airCreature.setLife(1);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
        // deal 1 damage to the air creature
        airCreature.dealDamage(1);
        System.out.println("AirCreature's life points: " + airCreature.getLife());
    }*/

    /*private void testAirCreatureCanMove() {
        // create an air creature
        AirCreature airCreature = new AirCreature();

        // set energy to 0
        airCreature.setEnergy(0);
        // print if the air creature can move
        System.out.println("AirCreature can move: " + airCreature.canMove());

        // set energy to 10
        airCreature.setEnergy(10);
        // print if the air creature can move
        System.out.println("AirCreature can move: " + airCreature.canMove());
    }*/

    public static void main(String args[]) {
        new Test().run();
    }
}
