package com.mygdx.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.functions.DoubleIntConsumer;

import java.util.Arrays;

import static com.mygdx.game.map.Textures.*;

public class WorldMap {
    public static final int MAP_WIDTH = 32;
    public static final int MAP_HEIGHT = 32;
    private static final int INITIAL_CAPACITY = MAP_WIDTH * MAP_HEIGHT / 4;
    private int prevSelectionRow = -1;
    private int prevSelectionCol = -1;
    private final Pool<Tile> selectionTilePool = new Pool<Tile>(INITIAL_CAPACITY, MAP_WIDTH * MAP_HEIGHT) {
        @Override
        protected Tile newObject() {
            return new Tile(GREEN_INDICATOR);
        }
    };
    private final Tile selector;
    private final Tile selected;

    private final Tile[][] tileMap;
    private final Tile[][] itemsMap;
    private final Tile[][] selectionLayer;

    private final MapLayer firstFloor;
    private final MapLayer secondFloor;
    private final MapLayer thirdFloor;
    public WorldMap() {
        tileMap = new Tile[MAP_WIDTH][MAP_HEIGHT];
        itemsMap = new Tile[MAP_WIDTH][MAP_HEIGHT];
        selectionLayer = new Tile[MAP_WIDTH][MAP_HEIGHT];

        firstFloor = new MapLayer();
        secondFloor = new MapLayer(1);
        thirdFloor = new MapLayer(2);

        selector = new Tile(Textures.WHITE_SELECTOR);
        selected = new Tile(Textures.YELLOW_SELECTOR);

        selectionTilePool.fill(INITIAL_CAPACITY);

//        generateMap();
        generateLayeredMap();
    }

    public void generateLayeredMap() {
        float[][] mapNoise = PerlinNoiseGenerator.generatePerlinNoise(MAP_WIDTH, MAP_HEIGHT, 2);
        // fill maps with nulls
        firstFloor.clearLayer();
        secondFloor.clearLayer();
        thirdFloor.clearLayer();

        for (int i = 0; i < mapNoise.length; i++) {
            for (int j = 0; j < mapNoise[0].length; j++) {
                float value = mapNoise[i][j];
                if (value < 0.2) {
                    thirdFloor.addTile(new Tile(GRASS), i, j);
                }
                if (value < 0.3) {
                    secondFloor.addTile(new Tile(GRASS), i, j);
                }
                if (value < 0.67) {
                    firstFloor.addTile(new Tile(GRASS), i, j);
                } else {
                    firstFloor.addTile(new Tile(WATER), i, j);
                }
            }
        }

    }

    public void generateMap() {
        generateMap(1);
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
                    tile.setCost((int) (tile.getCost() * 1.1f));
                    addItem(new Tile(PLANT, i + 1, j + 1));
                }
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
//        renderLevelMap(batch, delta);
        renderMapLayer(tileMap, batch, delta);
        renderMapLayer(itemsMap, batch, delta);
        selector.render(batch, delta);
        if (hasActiveSelection()) {
            selected.render(batch, delta);
        }
        renderMapLayer(selectionLayer, batch, delta);
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
        firstFloor.render(batch, delta);
        secondFloor.render(batch, delta);
        thirdFloor.render(batch, delta);
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

    public boolean isPassable(int row, int col) {
        // check for bounds
        if (row < 0 || row >= MAP_WIDTH || col < 0 || col >= MAP_HEIGHT) {
            return false;
        }
        return tileMap[row][col].isPassable();
    }

    public void selectTile(int row, int col) {
        if (row < 0 || row >= MAP_WIDTH || col < 0 || col >= MAP_HEIGHT) {
            return;
        }
        selected.setPosition(row, col);
        prevSelectionRow = row;
        prevSelectionCol = col;
    }

    public void selectTileRange(int startRow, int startCol,
                                int endRow, int endCol,
                                DoubleIntConsumer selectionCallback,
                                DoubleIntConsumer deselectCallback) {
        if (!isInBounds(startRow, startCol)) {
            return;
        }

        if (!isInBounds(endRow, endCol)) {
            return;
        }

        // set start row the min, end row the max
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }

        // set start col the min, end col the max
        if (startCol > endCol) {
            int temp = startCol;
            startCol = endCol;
            endCol = temp;
        }

        // clear previous selection
        clearRangeSelection(deselectCallback);

        // select new tiles
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                Tile tile = selectionTilePool.obtain();
                tile.setPosition(i + 1, j + 1);
                selectionLayer[i][j] = tile;
                selectionCallback.accept(i, j);
            }
        }
    }

    public void clearRangeSelection(DoubleIntConsumer deselectCallback) {
        for (int i = 0; i < selectionLayer.length; i++) {
            Tile[] tiles = selectionLayer[i];
            for (int j = 0; j < tiles.length; j++) {
                Tile tile = tiles[j];
                if (tile == null) continue;
                selectionTilePool.free(tile);
                selectionLayer[i][j] = null;
                deselectCallback.accept(i, j);
            }
        }
    }

    public void clearSelection() {
        prevSelectionRow = -1;
        prevSelectionCol = -1;
    }

    private boolean hasActiveSelection() {
        return prevSelectionRow != -1 && prevSelectionCol != -1;
    }

    public void highlightTile(int row, int col) {
        selector.setPosition(row + 1, col + 1);
        if (MyGdxGame.API().getWorld().isPassable(row, col)) {
            selector.setColor(Color.WHITE);
        } else {
            selector.setColor(Color.RED);
        }
    }

    private static class MapLayer {
        private final Tile[][] map;
        private int layer;

        public MapLayer() {
            this(0);
        }

        public MapLayer(int layer) {
            map = createMapLayer(layer);
        }

        private Tile[][] createMapLayer(int layer) {
            this.layer = layer;
            return new Tile[MAP_WIDTH][MAP_HEIGHT];
        }

        private void clearLayer() {
            for (Tile[] tiles : map) {
                Arrays.fill(tiles, null);
            }
        }

        public void addTile(Tile tile, int row, int col) {
            tile.setPosition(row + layer, col + layer);
            map[row][col] = tile;
        }

        private void render(SpriteBatch batch, float delta) {
            for (int i = map.length - 1; i >= 0; i--) {
                for (int j = map[i].length - 1; j >= 0; j--) {
                    Tile tile = map[i][j];
                    if (tile == null) continue;
                    tile.render(batch, delta);
                }
            }
        }
    }
}
