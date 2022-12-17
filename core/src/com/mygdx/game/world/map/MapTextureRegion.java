package com.mygdx.game.world.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MapTextureRegion extends TextureRegion {
    private final int index;

    public MapTextureRegion(TextureRegion[][] splitMap, int i, int j) {
        super(splitMap[i][j]);
        this.index = i * (splitMap.length + 1) + j;
    }

    public int getIndex() {
        return index;
    }
}
