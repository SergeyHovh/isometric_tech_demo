package com.mygdx.game.entities;

import com.mygdx.game.MyGdxGame;

public class MainPlayer extends DoubleSidedEntity {
    public MainPlayer() {
        super(0);
        speed = 20;
    }

    public void onTouchDown(float row, float col) {
        if (MyGdxGame.API().getWorld().isPassable((int) row, (int) col)) {
            findPathAndMove((int) row, (int) col);
            Entity entity = MyGdxGame.API().getWorld().containsEntity((int) row, (int) col);
            attackEntity((DoubleSidedEntity) entity);
        }
    }
}
