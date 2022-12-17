package com.mygdx.game.util;

import com.mygdx.game.world.map.Tile;
import com.mygdx.game.world.map.WorldMap;

public class MapSaver {
    private final WorldMap worldMap;

    public MapSaver(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public void saveMap() {
        // TODO: 12/17/2022 implement a proper map saving, maybe save into a file or something
        for (Tile[] tiles : worldMap.getTileMap()) {
            for (Tile tile : tiles) {
                System.out.print(tile.getRegion().getIndex() + " ");
            }
            System.out.println();
        }
    }

}
