package com.mygdx.game.world.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.game.util.Constants.Tile.TEXTURE_TILE_HEIGHT;
import static com.mygdx.game.util.Constants.Tile.TEXTURE_TILE_WIDTH;

public class Textures {

    private static final TextureRegion[][] mapTextures;
    private static final TextureRegion[][] mapIndicators;
    public static final TextureRegion[][] entities;
    public static final TextureRegion[][] highlightedEntities;

    static {
        mapIndicators = TextureRegion.split(new Texture("IsometricTRPGAssetPack_MapIndicators.png"), 16, 8);
        mapTextures = TextureRegion.split(new Texture("Isometric_MedievalFantasy_Tiles.png"), TEXTURE_TILE_WIDTH, TEXTURE_TILE_HEIGHT);
        entities = TextureRegion.split(new Texture("IsometricTRPGAssetPack_Entities.png"), TEXTURE_TILE_WIDTH, TEXTURE_TILE_HEIGHT);
        highlightedEntities = TextureRegion.split(new Texture("IsometricTRPGAssetPack_OutlinedEntities.png"), TEXTURE_TILE_WIDTH, TEXTURE_TILE_HEIGHT);
    }

    public static final MapTextureRegion GREEN_INDICATOR = new MapTextureRegion(mapIndicators, 0, 0);
    public static final MapTextureRegion WHITE_SELECTOR = new MapTextureRegion(mapIndicators, 2, 0);
    public static final MapTextureRegion YELLOW_SELECTOR = new MapTextureRegion(mapIndicators, 2, 1);

    public static final MapTextureRegion DIRT = new MapTextureRegion(mapTextures, 0, 0);
    public static final MapTextureRegion GRASS = new MapTextureRegion(mapTextures, 0, 1);
    public static final MapTextureRegion STONE = new MapTextureRegion(mapTextures, 0, 2);
    public static final MapTextureRegion SAND = new MapTextureRegion(mapTextures, 0, 3);
    public static final MapTextureRegion WATER = new MapTextureRegion(mapTextures, 0, 4);
    public static final MapTextureRegion PLANT = new MapTextureRegion(mapTextures, 0, 10);
    public static final MapTextureRegion BRIDGE_LEFT = new MapTextureRegion(mapTextures, 8, 8);
    public static final MapTextureRegion BRIDGE_RIGHT =  new MapTextureRegion(mapTextures, 8, 9);

    public static int getRegionIndex(int i, int j) {
        return i * (mapTextures.length + 1) + j;
    }

    public static void dispose() {

    }
}
