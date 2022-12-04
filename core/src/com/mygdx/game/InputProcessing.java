package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.game.events.MapClickEvent;
import com.mygdx.game.events.MouseDraggedEvent;
import com.mygdx.game.events.MouseMovedEvent;
import com.mygdx.game.events.management.EventManager;

public class InputProcessing implements InputProcessor {

    private final IntMap<Runnable> keyDownMap;
    private final IntMap<Runnable> keyUpMap;
    private final IntMap<Runnable> keyTypedMap;
    private boolean selectByDrag = false;
    private float selectStartX = -1, selectStartY = -1;

    public InputProcessing() {
        keyDownMap = new IntMap<>();
        keyUpMap = new IntMap<>();
        keyTypedMap = new IntMap<>();

        keyDownMap.put(Input.Keys.SHIFT_LEFT, () -> {
            selectByDrag = true;
        });

        keyUpMap.put(Input.Keys.SHIFT_LEFT, () -> {
            selectByDrag = false;
        });

        keyUpMap.put(Input.Keys.ESCAPE, Gdx.app::exit);
    }

    @Override
    public boolean keyDown(int keycode) {
        keyDownMap.get(keycode, () -> {
        }).run();
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyUpMap.get(keycode, () -> {
        }).run();
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        keyTypedMap.get(character, () -> {
        }).run();
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (selectByDrag) {
            selectStartX = screenX;
            selectStartY = screenY;
            return false;
        }
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
        selectStartX = -1;
        selectStartY = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        MouseDraggedEvent mouseDraggedEvent = EventManager.getInstance().obtainEvent(MouseDraggedEvent.class);
        mouseDraggedEvent.setStartPosition(CoordinateUtils.screenToTile(selectStartX, selectStartY));
        mouseDraggedEvent.setEndPosition(CoordinateUtils.screenToTile(screenX, screenY));
        mouseDraggedEvent.setSelectByDrag(selectByDrag);
        EventManager.getInstance().fireEvent(mouseDraggedEvent);
        if (!selectByDrag) {
            MyGdxGame.API().getGameScreen().moveCamera();
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        MouseMovedEvent mouseMovedEvent = EventManager.getInstance().obtainEvent(MouseMovedEvent.class);
        mouseMovedEvent.setPosition(CoordinateUtils.screenToTile(screenX, screenY));
        EventManager.getInstance().fireEvent(mouseMovedEvent);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        MyGdxGame.API().getGameScreen().zoomCamera(amountX, amountY);
        return false;
    }

    private boolean hasActiveSelection() {
        return selectStartX != -1 && selectStartY != -1;
    }
}
