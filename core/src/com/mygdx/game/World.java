package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.DoubleSidedEntity;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.EntityFactory;
import com.mygdx.game.entities.MainPlayer;
import com.mygdx.game.map.PathFinder;
import com.mygdx.game.map.Textures;
import com.mygdx.game.map.Tile;
import com.mygdx.game.map.WorldMap;

import java.util.Comparator;

public class World {
    private final WorldMap worldMap;
    private final Array<Entity> entities;
    private MainPlayer mainPlayer;
    private final Comparator<Entity> isometricComparator;

    private final Tile selector;
    private final Tile selected;
    private boolean tileSelected = false;

    public World() {
        worldMap = new WorldMap();
        worldMap.generateMap(1);
        entities = new Array<>();
        selector = new Tile(Textures.WHITE_SELECTOR, 0, 0, true, 0);
        selected = new Tile(Textures.YELLOW_SELECTOR, 0, 0, true, 0);
        isometricComparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return -(int) (o1.getY() - o2.getY());
            }
        };
        PathFinder.generateGraph(worldMap.getTileMap(), true);
    }

    public void show() {
        mainPlayer = new MainPlayer();
        for (int i = 0; i < 10; i++) {
            DoubleSidedEntity bat = EntityFactory.BAT();
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
        for (Entity entity : entities.toArray(Entity.class)) {
            entity.render(batch, delta);
        }
    }

    public void tick() {

    }

    public void registerEntity(Entity entity) {
        entities.add(entity);
    }

    public boolean isPassable(int row, int col) {
        return worldMap.isPassable(row, col);
    }

    public boolean isInBounds(int row, int col) {
        return worldMap.isInBounds(row, col);
    }

    public void touchDown(Vector2 tile) {
/*        for (Entity entity : entities.toArray(Entity.class)) {
            float dx = entity.getTileX() - tile.x;
            float dy = entity.getTileY() - tile.y;
            if (dx * dx + dy * dy < 1) {
                entity.select();
                break;
            }
        }*/
    }

    public void rightClickDown(int row, int col) {
        mainPlayer.onTouchDown(row, col);
    }

    public void selectTile(int row, int col) {
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

    public Entity containsEntity(int x, int y) {
        for (Entity entity : entities.toArray(Entity.class)) {
            if (entity.getTileX() == x && entity.getTileY() == y) {
                return entity;
            }
        }
        return null;
    }
}
