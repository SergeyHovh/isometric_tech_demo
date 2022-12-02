package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

public class InputProcessing implements InputProcessor {

    private final IntMap<Runnable> controls;

    public InputProcessing() {
        controls = new IntMap<>();

        controls.put(Input.Keys.A, new Runnable() {
            @Override
            public void run() {
                MyGdxGame.API().getGameScreen().worldMap.generateMap(1);
            }
        });

        controls.put(Input.Keys.S, new Runnable() {
            @Override
            public void run() {
                MyGdxGame.API().getGameScreen().worldMap.generateMap(2);
            }
        });

        controls.put(Input.Keys.D, new Runnable() {
            @Override
            public void run() {
                MyGdxGame.API().getGameScreen().worldMap.generateMap(3);
            }
        });
    }

    @Override
    public boolean keyDown(int keycode) {
        controls.get(keycode, new Runnable() {
            @Override
            public void run() {

            }
        }).run();
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            MyGdxGame.API().getGameScreen().moveCamera();
        }
        if (button == Input.Buttons.RIGHT) {
            Vector2 screenToTile = CoordinateUtils.screenToTile(screenX, screenY);
            int row = (int) screenToTile.x;
            int col = (int) screenToTile.y;
            MyGdxGame.API().getGameScreen().selectTile(row, col);
            MyGdxGame.API().getGameScreen().rightClickDown(row, col);
        }
        MyGdxGame.API().getGameScreen().touchDown(CoordinateUtils.screenToWorld(screenX, screenY));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        MyGdxGame.API().getGameScreen().moveCamera();
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector2 screenToTile = CoordinateUtils.screenToTile(screenX, screenY);
        MyGdxGame.API().getGameScreen().highlightTile(screenToTile);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        MyGdxGame.API().getGameScreen().zoomCamera(amountX, amountY);
        return false;
    }
}
