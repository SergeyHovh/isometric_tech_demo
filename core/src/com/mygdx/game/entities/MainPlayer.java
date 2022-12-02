package com.mygdx.game.entities;

public class MainPlayer extends DoubleSidedEntity {
    public MainPlayer() {
        super(0);
        speed = 20;
    }

    public void onTouchDown(float row, float col) {
        findPathAndMove((int) row, (int) col);
    }
}
