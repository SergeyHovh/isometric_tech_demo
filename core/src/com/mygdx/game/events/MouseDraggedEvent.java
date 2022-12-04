package com.mygdx.game.events;

import com.badlogic.gdx.math.Vector2;

public class MouseDraggedEvent extends Event {

    private final Vector2 startPosition = new Vector2(0, 0);
    private final Vector2 endPosition = new Vector2(0, 0);
    private boolean selectByDrag;
    private int startRow, startCol, endRow, endCol;

    public void setEndPosition(Vector2 position) {
        endPosition.set(position);
        endRow = (int) endPosition.x;
        endCol = (int) endPosition.y;
    }

    public void setStartPosition(Vector2 position) {
        startPosition.set(position);
        startRow = (int) startPosition.x;
        startCol = (int) startPosition.y;
    }

    public boolean isSelectByDrag() {
        return selectByDrag;
    }

    public void setSelectByDrag(boolean selectByDrag) {
        this.selectByDrag = selectByDrag;
    }

    // getters
    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    @Override
    public void reset() {
        startRow = -1;
        startCol = -1;
        endRow = -1;
        endCol = -1;
        startPosition.set(-1, -1);
        endPosition.set(-1, -1);
    }
}
