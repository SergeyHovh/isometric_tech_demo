package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.AnimatedGameActor;
import com.mygdx.game.entities.GameActor;
import com.mygdx.game.entities.GameActorFactory;
import com.mygdx.game.entities.MainPlayer;
import com.mygdx.game.events.MapClickEvent;
import com.mygdx.game.events.MouseDraggedEvent;
import com.mygdx.game.events.MouseMovedEvent;
import com.mygdx.game.events.management.EventHandler;
import com.mygdx.game.events.management.EventManager;
import com.mygdx.game.events.management.Observer;
import com.mygdx.game.world.map.PathFinder;
import com.mygdx.game.world.map.WorldMap;

import java.util.Comparator;

public class World implements Observer {
    public final WorldMap worldMap;
    private final Array<GameActor> entities;
    private final Comparator<GameActor> isometricComparator;
    private MainPlayer mainPlayer;

    public World() {
        EventManager.getInstance().registerObserver(this);
        worldMap = new WorldMap();
        worldMap.generateMap(2);
        entities = new Array<>();
        isometricComparator = (o1, o2) -> Float.compare(o2.getY(), o1.getY());
        PathFinder.generateGraph(worldMap.getTileMap(), true);
    }

    public void show() {
        mainPlayer = new MainPlayer();
        for (int i = 0; i < 10; i++) {
            AnimatedGameActor bat = GameActorFactory.BAT();
            bat.setPosition(MathUtils.random(5, 20), MathUtils.random(5, 20));
        }
    }

    public void render(SpriteBatch batch, float delta) {
        worldMap.render(batch, delta);
        entities.sort(isometricComparator);
        for (GameActor gameActor : entities.toArray(GameActor.class)) {
            gameActor.render(batch, delta);
        }
    }

    public void tick() {
        for (GameActor gameActor : entities.toArray(GameActor.class)) {
            if (gameActor == mainPlayer) continue;
            ((AnimatedGameActor) gameActor).followGameActor(mainPlayer);
        }
    }

    public void registerEntity(GameActor gameActor) {
        entities.add(gameActor);
    }

    public boolean isPassable(int row, int col) {
        return worldMap.isPassable(row, col);
    }

    public boolean isInBounds(int row, int col) {
        return worldMap.isInBounds(row, col);
    }

    public void selectActor(Vector2 tile) {
        for (GameActor entity : entities.toArray(GameActor.class)) {
            float dx = entity.getTileX() - tile.x;
            float dy = entity.getTileY() - tile.y;
            if (dx * dx + dy * dy < 1) {
                entity.select();
                break;
            }
        }
    }

    private void selectTile(int row, int col) {
        if (!MyGdxGame.API().getWorld().isInBounds(row, col)) return;
        if (!MyGdxGame.API().getWorld().isPassable(row, col)) return;
        worldMap.selectTile(row + 1, col + 1);
    }

    public void highlightTile(Vector2 screenToTile) {
        worldMap.highlightTile((int) screenToTile.x, (int) screenToTile.y);
    }

    public GameActor containsGameActor(int x, int y) {
        for (GameActor gameActor : entities.toArray(GameActor.class)) {
            if (gameActor.getTileX() == x && gameActor.getTileY() == y) {
                return gameActor;
            }
        }
        return null;
    }

    @EventHandler
    public void onMapClickEvent(MapClickEvent event) {
        if (event.getClickButton() == MapClickEvent.MouseClickButton.RIGHT) {
            selectTile(event.getRow(), event.getCol());
        }
        if (event.getClickButton() == MapClickEvent.MouseClickButton.LEFT) {
            selectActor(event.getClickPosition());
            worldMap.clearSelection();
        }
        worldMap.clearRangeSelection((row, col) -> {
            GameActor gameActor = containsGameActor(row, col);
            if (gameActor != null) {
                gameActor.deselect();
            }
        });
    }

    @EventHandler
    public void onMouseMovedEvent(MouseMovedEvent event) {
        highlightTile(event.getPosition());
    }

    private int prevEndRow = -1;
    private int prevEndCol = -1;

    @EventHandler
    public void onMouseDraggedEvent(MouseDraggedEvent event) {
        if (!event.isSelectByDrag()) return;
        int endRow = event.getEndRow();
        int endCol = event.getEndCol();
        if (prevEndRow == endRow && prevEndCol == endCol) return;

        worldMap.selectTileRange(
                event.getStartRow(), event.getStartCol(),
                event.getEndRow(), event.getEndCol(),
                (row, col) -> {
                    GameActor gameActor = containsGameActor(row, col);
                    if (gameActor != null) {
                        gameActor.select();
                    }
                }, (row, col) -> {
                    GameActor gameActor = containsGameActor(row, col);
                    if (gameActor != null) {
                        gameActor.deselect();
                    }
                });
    }
}
