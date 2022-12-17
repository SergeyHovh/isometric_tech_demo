package com.mygdx.game;

import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.world.World;

public class API {
    private GameScreen gameScreen;
    private World world;

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
