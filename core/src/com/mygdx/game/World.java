package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.AnimatedGameActor;
import com.mygdx.game.entities.GameActor;
import com.mygdx.game.entities.GameActorFactory;
import com.mygdx.game.entities.MainPlayer;
import com.mygdx.game.events.MapClickEvent;
import com.mygdx.game.events.MouseMovedEvent;
import com.mygdx.game.events.management.EventHandler;
import com.mygdx.game.events.management.EventManager;
import com.mygdx.game.events.management.Observer;
import com.mygdx.game.map.PathFinder;
import com.mygdx.game.map.Textures;
import com.mygdx.game.map.Tile;
import com.mygdx.game.map.WorldMap;

import java.util.Comparator;

public class World implements Observer {
    public final WorldMap worldMap;
    private final Array<GameActor> entities;
    private MainPlayer mainPlayer;
    private final Comparator<GameActor> isometricComparator;

    private final Tile selector;
    private final Tile selected;
    private boolean tileSelected = false;

    public World() {
        EventManager.getInstance().registerObserver(this);
        worldMap = new WorldMap();
        worldMap.generateMap(1);
        entities = new Array<>();
        selector = new Tile(Textures.WHITE_SELECTOR, 0, 0, true, 0);
        selected = new Tile(Textures.YELLOW_SELECTOR, 0, 0, true, 0);
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
        selector.render(batch, delta);
        if (tileSelected) {
            selected.render(batch, delta);
        }
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
        tileSelected = true;
        selected.setPosition(row + 1, col + 1);
    }

    public void highlightTile(Vector2 screenToTile) {
        // add out of bounds checks
        Vector2 position = screenToTile.add(1, 1);
        if (position.x < 1) position.x = 1;
        if (position.x > WorldMap.MAP_WIDTH) position.x = WorldMap.MAP_WIDTH;
        if (position.y < 1) position.y = 1;
        if (position.y > WorldMap.MAP_HEIGHT) position.y = WorldMap.MAP_HEIGHT;
        selector.setPosition(position);
        if (MyGdxGame.API().getWorld().isPassable((int) (position.x - 1), (int) (position.y - 1))) {
            selector.setColor(Color.WHITE);
        } else{
            selector.setColor(Color.RED);
        }
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
        }
    }

    @EventHandler
    public void onMouseMovedEvent(MouseMovedEvent event) {
        highlightTile(event.getPosition());
    }
}
