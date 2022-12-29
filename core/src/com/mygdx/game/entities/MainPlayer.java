package com.mygdx.game.entities;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.events.MapClickEvent;
import com.mygdx.game.events.management.EventHandler;

public class MainPlayer extends AnimatedGameActor {
    public MainPlayer() {
        super(0);
        speed = 3;
    }

    @EventHandler
    public void onMapClickEvent(MapClickEvent event) {
        int row = event.getRow();
        int col = event.getCol();
        if (event.getClickButton() == MapClickEvent.MouseClickButton.RIGHT) {
            findPathAndMove(row, col);
            GameActor gameActor = MyGdxGame.API().getWorld().containsGameActor(row, col);
            attackEntity((AnimatedGameActor) gameActor);
        }
    }
}
