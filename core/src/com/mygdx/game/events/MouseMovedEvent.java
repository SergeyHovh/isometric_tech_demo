package com.mygdx.game.events;

import com.badlogic.gdx.math.Vector2;

public class MouseMovedEvent extends Event {
    private final Vector2 mousePosition = new Vector2(0, 0);

    private int row, col;

    public void setPosition(Vector2 position) {
        mousePosition.set(position);
        row = (int) mousePosition.x;
        col = (int) mousePosition.y;
    }

    // getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Vector2 getPosition() {
        return mousePosition;
    }

    @Override
    public void reset() {
        row = -1;
        col = -1;
        mousePosition.set(-1, -1);
    }
}
