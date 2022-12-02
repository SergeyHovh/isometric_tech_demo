package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.mygdx.game.map.Textures.*;

public class WorldMap {
    public static final int MAP_WIDTH = 20;
    public static final int MAP_HEIGHT = 20;
    private final Tile[][] tileMap;
    private final Tile[][] itemsMap;

    private final Tile[][] firstFloor;
    private final Tile[][] secondFloor;
    private final Tile[][] thirdFloor;

    public WorldMap() {
        tileMap = new Tile[MAP_WIDTH][MAP_HEIGHT];
        itemsMap = new Tile[MAP_WIDTH][MAP_HEIGHT];

        firstFloor = new Tile[MAP_WIDTH][MAP_HEIGHT];
        secondFloor = new Tile[MAP_WIDTH][MAP_HEIGHT];
        thirdFloor = new Tile[MAP_WIDTH][MAP_HEIGHT];
        generateMap();
    }

    public void generateLayeredMap() {
        float[][] mapNoise = PerlinNoiseGenerator.generatePerlinNoise(MAP_WIDTH, MAP_HEIGHT, 2);
        // fill maps with nulls
        for (int row = 0; row < MAP_WIDTH; row++) {
            for (int col = 0; col < MAP_HEIGHT; col++) {
                firstFloor[row][col] = null;
                secondFloor[row][col] = null;
                thirdFloor[row][col] = null;
            }
        }

        for (int i = 0; i < mapNoise.length; i++) {
            for (int j = 0; j < mapNoise[0].length; j++) {
                float value = mapNoise[i][j];
                if (value < 0.2) {
                    thirdFloor[i][j] = new Tile(GRASS, i + 2, j + 2, true, 1);
                }
                if (value < 0.3) {
                    secondFloor[i][j] = new Tile(GRASS, i + 1, j + 1, true, 1);
                }
                if (value < 0.67) {
                    firstFloor[i][j] = new Tile(GRASS, i, j, true, 1);
                } else {
                    firstFloor[i][j] = new Tile(WATER, i, j, false, 0);
                }
            }
        }

    }

    public void generateMap() {
        generateMap(3);
    }

    public void generateMap(int octaveCount) {
        float[][] mapNoise = PerlinNoiseGenerator.generatePerlinNoise(MAP_WIDTH, MAP_HEIGHT, octaveCount);
        // fill maps with nulls
        for (int row = 0; row < MAP_WIDTH; row++) {
            for (int col = 0; col < MAP_HEIGHT; col++) {
                tileMap[row][col] = null;
                itemsMap[row][col] = null;
            }
        }

        for (int i = 0; i < mapNoise.length; i++) {
            for (int j = 0; j < mapNoise[0].length; j++) {
                float value = mapNoise[i][j];
                if (value < 0.1f) {
                    tileMap[i][j] = new Tile(SAND, i, j, true, 40);
                } else if (value < 0.7f) {
                    tileMap[i][j] = new Tile(GRASS, i, j, true, 10);
                } else {
                    tileMap[i][j] = new Tile(WATER, i, j, false, 0);
                }
            }
        }

        float[][] itemsMap = PerlinNoiseGenerator.generatePerlinNoise(MAP_WIDTH, MAP_HEIGHT, 3);

        for (int i = 0; i < itemsMap.length; i++) {
            float[] rows = itemsMap[i];
            for (int j = 0; j < rows.length; j++) {
                float value = rows[j];
                Tile tile = tileMap[i][j];
                if (value < 0.4f && tile.isPassable()) {
                    tile.setCost((int) (tile.getCost() * 1.5f));
                    tile.setPassable(true);
                    addItem(new Tile(PLANT, i + 1, j + 1, true, 0));
                }

//                float mapValue = mapNoise[i][j];
//                if (mapValue > 0.98f) {
//                    if (mapValue < 0.99f) {
//                        generateLeftBridge(i, j);
//                    } else {
//                        generateRightBridge(i, j);
//                    }
//                }
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        renderMap(batch, delta);
        renderItems(batch, delta);
    }

    public void addItem(Tile item) {
        int row = item.getRow();
        int col = item.getCol();
        // check for out of bounds
        if (row < 0 || row >= MAP_WIDTH || col < 0 || col >= MAP_HEIGHT) {
            return;
        }
        itemsMap[row][col] = item;
    }

    private void renderMap(SpriteBatch batch, float delta) {
        renderMapLayer(tileMap, batch, delta);
    }

    private void renderItems(SpriteBatch batch, float delta) {
        renderMapLayer(itemsMap, batch, delta);
    }

    private void renderLevelMap(SpriteBatch batch, float delta) {
        renderMapLayer(firstFloor, batch, delta);
        renderMapLayer(secondFloor, batch, delta);
        renderMapLayer(thirdFloor, batch, delta);
    }

    private void renderMapLayer(Tile[][] map, SpriteBatch batch, float delta) {
        for (int i = map.length - 1; i >= 0; i--) {
            for (int j = map[i].length - 1; j >= 0; j--) {
                if (map[i][j] == null) continue;
                map[i][j].render(batch, delta);
            }
        }
    }

    public Tile getMapTile(int row, int col) {
        return tileMap[row][col];
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < MAP_WIDTH && col >= 0 && col < MAP_HEIGHT;
    }

    private void generateLeftBridge(int row, int col) {
        int k = 0;
        while (col + k < MAP_HEIGHT && !tileMap[row][col + k].isPassable()) {
            itemsMap[row][col + k] = new Tile(BRIDGE_LEFT, row, col + k, true, 15);
            tileMap[row][col + k].setPassable(true);
            tileMap[row][col + k].setCost(1);
            k++;
        }

        k = 1;
        while (col - k >= 0 && !tileMap[row][col - k].isPassable()) {
            itemsMap[row][col - k] = new Tile(BRIDGE_LEFT, row, col - k, true, 15);
            tileMap[row][col - k].setPassable(true);
            tileMap[row][col - k].setCost(1);
            k++;
        }
    }

    private void generateRightBridge(int row, int col) {
        int k = 0;
        while (row + k < MAP_WIDTH && !tileMap[row + k][col].isPassable()) {
            itemsMap[row + k][col] = new Tile(BRIDGE_RIGHT, row + k, col, true, 15);
            tileMap[row + k][col].setPassable(true);
            tileMap[row + k][col].setCost(1);
            k++;
        }

        k = 1;
        while (row - k >= 0 && !tileMap[row - k][col].isPassable()) {
            itemsMap[row - k][col] = new Tile(BRIDGE_RIGHT, row - k, col, true, 15);
            tileMap[row - k][col].setPassable(true);
            tileMap[row - k][col].setCost(1);
            k++;
        }
    }
}
