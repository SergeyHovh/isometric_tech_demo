package com.mygdx.game.entities;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.events.MapClickEvent;
import com.mygdx.game.events.management.EventHandler;
import com.mygdx.game.events.management.EventManager;
import com.mygdx.game.events.management.Observer;

public class MainPlayer extends AnimatedGameActor implements Observer {
    public MainPlayer() {
        super(0);
        EventManager.getInstance().registerObserver(this);
        speed = 3;
    }

    @EventHandler
    public void onMapClickEvent(MapClickEvent event) {
        int row = event.getRow();
        int col = event.getCol();
        if (event.getClickButton() == MapClickEvent.MouseClickButton.RIGHT) {
            if (MyGdxGame.API().getWorld().isPassable(row, col)) {
                findPathAndMove(row, col);
                GameActor gameActor = MyGdxGame.API().getWorld().containsGameActor(row, col);
                attackEntity((AnimatedGameActor) gameActor);
            }
        }
    }
}
