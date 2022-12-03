package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.game.events.EventManager;
import com.mygdx.game.events.MapClickEvent;

public class InputProcessing implements InputProcessor {

    private final IntMap<Runnable> controls;

    public InputProcessing() {
        controls = new IntMap<>();
        controls.put(Input.Keys.A, () -> {
        });

        controls.put(Input.Keys.S, () -> {
        });
    }

    @Override
    public boolean keyDown(int keycode) {
        controls.get(keycode, () -> {
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
        MapClickEvent mapClickEvent = EventManager.getInstance().obtainEvent(MapClickEvent.class);
        mapClickEvent.setPosition(CoordinateUtils.screenToTile(screenX, screenY));
        if (button == Input.Buttons.LEFT) {
            mapClickEvent.setClickButton(MapClickEvent.MouseClickButton.LEFT);
        }
        if (button == Input.Buttons.RIGHT) {
            mapClickEvent.setClickButton(MapClickEvent.MouseClickButton.RIGHT);
        }
        EventManager.getInstance().fireEvent(mapClickEvent);
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
        MyGdxGame.API().getWorld().highlightTile(screenToTile);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        MyGdxGame.API().getGameScreen().zoomCamera(amountX, amountY);
        return false;
    }
}
