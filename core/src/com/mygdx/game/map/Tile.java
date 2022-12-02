package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.CoordinateUtils;
import com.mygdx.game.GameScreen;

public class Tile {
    private TextureRegion region;
    private int row, col;
    private float x, y;
    private boolean passable;
    private int cost;

    public Tile(TextureRegion region, int row, int col, boolean passable, int cost) {
        this.region = region;
        this.row = row;
        this.col = col;
        this.passable = passable;
        this.cost = cost;
        convert();
    }

    public void render(SpriteBatch batch, float delta) {
        batch.draw(region, x, y);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setPosition(Vector2 position) {
        row = (int) position.x;
        col = (int) position.y;
        convert();
    }

    private void convert() {
        Vector2 screenPos = CoordinateUtils.tileToScreen(row - 1, col - 1);
        x = screenPos.x;
        y = screenPos.y;
    }
}
