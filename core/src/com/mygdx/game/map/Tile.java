package com.mygdx.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ColorUtil;
import com.mygdx.game.CoordinateUtils;

public class Tile {
    private TextureRegion region;
    private int row, col;
    private float x, y;
    private boolean passable;
    private int cost;
    private Color color = Color.WHITE;
    private final Color tmpColor = new Color();

    public Tile(TextureRegion region) {
        this(region, -1, -1);
    }

    public Tile(TextureRegion region, int row, int col) {
        this(region, row, col, true, 0);
    }

    public Tile(TextureRegion region, int row, int col, boolean passable, int cost) {
        this.region = region;
        this.row = row;
        this.col = col;
        this.passable = passable;
        this.cost = cost;
        convertCoordinates();
    }

    public void render(SpriteBatch batch, float delta) {
        if (color != Color.WHITE) {
            ColorUtil.copyColor(tmpColor, batch.getColor());
            batch.setColor(color);
            batch.draw(region, x, y);
            batch.setColor(tmpColor);
        } else {
            batch.draw(region, x, y);
        }
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
        convertCoordinates();
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
        convertCoordinates();
    }

    private void convertCoordinates() {
        Vector2 screenPos = CoordinateUtils.tileToScreen(row - 1, col - 1);
        x = screenPos.x;
        y = screenPos.y;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
