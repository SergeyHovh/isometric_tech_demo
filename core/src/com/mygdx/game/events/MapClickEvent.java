package com.mygdx.game.events;

import com.badlogic.gdx.math.Vector2;

public class MapClickEvent extends Event {
    private final Vector2 clickPositionVector = new Vector2(0, 0);

    public enum MouseClickButton {
        LEFT, RIGHT
    }

    private int row, col;
    private MouseClickButton clickButton;

    public void setPosition(Vector2 position) {
        clickPositionVector.set(position);
        row = (int) clickPositionVector.x;
        col = (int) clickPositionVector.y;
    }

    // getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Vector2 getClickPosition() {
        return clickPositionVector;
    }

    public MouseClickButton getClickButton() {
        return clickButton;
    }

    public void setClickButton(MouseClickButton clickButton) {
        this.clickButton = clickButton;
    }

    @Override
    public void reset() {
        row = -1;
        col = -1;
        clickPositionVector.set(-1, -1);
        clickButton = MouseClickButton.LEFT;
    }
}
