package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.map.Textures;

public class CoordinateUtils {
    private static Vector3 tempVec3 = new Vector3();
    private static final Vector2 tempVec2 = new Vector2();

    public static Vector2 screenToWorld(float x, float y) {
        tempVec3.set(x, y, 0);
        tempVec3 = MyGdxGame.API().getGameScreen().camera.unproject(tempVec3);
        tempVec2.set(tempVec3.x, tempVec3.y);
        return tempVec2;
    }

    public static Vector2 tileToScreen(int row, int col) {
        float x = (row - col) * Textures.TEXTURE_TILE_WIDTH / 2.001f;
        float y = (row + col) * Textures.TEXTURE_TILE_HEIGHT / 4f;
        return new Vector2(x, y);
    }

    public static Vector2 screenToTile(float screenX, float screenY) {
        Vector2 world = screenToWorld(screenX, screenY);
        float alpha = Textures.TEXTURE_TILE_WIDTH / 2f;
        float beta = Textures.TEXTURE_TILE_HEIGHT / 4f;

        float x = world.x - 6.4f;
        float y = world.y - 4.8f;
        float row = MathUtils.round(0.5f * (x / alpha + y / beta));
        float col = MathUtils.round(0.5f * (y / beta - x / alpha));
        return new Vector2(row, col);
    }

}
