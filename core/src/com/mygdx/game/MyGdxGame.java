package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.events.management.EventManager;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.world.World;

public class MyGdxGame extends Game {

    private static API api;

    public static API API() {
        if (api == null) {
            api = new API();
        }
        return api;
    }

    @Override
    public void create() {
        GameScreen gameScreen = new GameScreen(new SpriteBatch());
        World world = new World();
        API().setWorld(world);
        API().setGameScreen(gameScreen);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        API().getGameScreen().dispose();
        EventManager.getInstance().dispose();
        api = null;
    }
}
