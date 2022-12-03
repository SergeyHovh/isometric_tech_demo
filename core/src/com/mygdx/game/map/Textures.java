package com.mygdx.game.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {
    public static final int TEXTURE_TILE_WIDTH = 16;
    public static final int TEXTURE_TILE_HEIGHT = 17;

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

    public static final TextureRegion GREEN_INDICATOR = mapIndicators[0][0];
    public static final TextureRegion WHITE_SELECTOR = mapIndicators[2][0];
    public static final TextureRegion YELLOW_SELECTOR = mapIndicators[2][1];

    public static final TextureRegion DIRT = mapTextures[0][0];
    public static final TextureRegion GRASS = mapTextures[0][1];
    public static final TextureRegion STONE = mapTextures[0][2];
    public static final TextureRegion SAND = mapTextures[0][3];
    public static final TextureRegion WATER = mapTextures[0][4];
    public static final TextureRegion PLANT = mapTextures[0][10];

    public static final TextureRegion dirt = mapTextures[1][0];
    public static final TextureRegion grass = mapTextures[1][1];
    public static final TextureRegion stone = mapTextures[1][2];
    public static final TextureRegion sand = mapTextures[1][3];
    public static final TextureRegion water = mapTextures[1][4];

    public static final TextureRegion BRIDGE_LEFT = mapTextures[8][8];
    public static final TextureRegion BRIDGE_RIGHT = mapTextures[8][9];

    public static void dispose() {

    }
}
