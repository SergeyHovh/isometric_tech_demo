package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.MainPlayer;
import com.mygdx.game.map.PathFinder;
import com.mygdx.game.map.Textures;
import com.mygdx.game.map.Tile;
import com.mygdx.game.map.WorldMap;

import java.util.Comparator;

public class GameScreen implements Screen {

    public static final int width = 1600;
    public static final int height = 900;
    private static final float cameraOffsetX = 0;
    private static final float cameraOffsetY = 0;
    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 0.3f;

    private final SpriteBatch batch;
    public final OrthographicCamera camera;
    private final Comparator<Entity> isometricComparator;
    public MainPlayer mainPlayer;
//    private Entity archer;
    public final WorldMap worldMap;
    private final Array<Entity> entities;
    private float time = 1f;
    private boolean stickToPlayer = true;

    private final Tile selector;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        entities = new Array<>();
        worldMap = new WorldMap();
        Gdx.input.setInputProcessor(new InputProcessing());
        camera = new OrthographicCamera(width, height);
        isometricComparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return -(int) (o1.getY() - o2.getY());
            }
        };
        PathFinder.generateGraph(worldMap.getTileMap(), true);

        selector = new Tile(Textures.WHITE_SELECTOR, 0, 0, true, 0);
    }

    @Override
    public void show() {
        camera.zoom = 0.2f;
        mainPlayer = new MainPlayer();
//        archer = EntityFactory.ARCHER_ENEMY();
//        for (int i = 0; i < 5; i++) {
//            DoubleSidedEntity bat = EntityFactory.BAT();
//            bat.setPosition(MathUtils.random(3, 20), MathUtils.random(3, 20));
//        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(35 / 255f, 122 / 255f, 148 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float offsetX = cameraOffsetX * camera.zoom;
        float offsetY = cameraOffsetY * camera.zoom;
        if (stickToPlayer) {
            camera.position.lerp(new Vector3(mainPlayer.getX() + offsetX, mainPlayer.getY() + offsetY, camera.position.z), 0.05f);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldMap.render(batch, delta);
        selector.render(batch, delta);
        entities.sort(isometricComparator);
        for (Entity entity : entities) {
            entity.render(batch, delta);
        }

        batch.end();

        time += delta;
        if (time >= 0.5f + delta / 2) {
            time = 0;
            tick();
        }
    }

    private void tick() {
//        bat.followEntity(mainPlayer);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        mainPlayer.dispose();
        Textures.dispose();
    }

    public void moveCamera() {
        int deltaX = Gdx.input.getDeltaX();
        int deltaY = Gdx.input.getDeltaY();
        stickToPlayer = false;
        camera.translate(-deltaX * camera.zoom, deltaY * camera.zoom);
    }

    public void zoomCamera(float amountX, float amountY) {
        camera.zoom += amountY * 0.1f;
        camera.zoom = MathUtils.clamp(camera.zoom, MIN_ZOOM, MAX_ZOOM);
    }

    public void selectTile(int row, int col) {
        if (!worldMap.isInBounds(row, col)) return;
        final Tile mapTile = worldMap.getMapTile(row, col);
        final TextureRegion prevRegion = mapTile.getRegion();
        mapTile.setRegion(Textures.STONE);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                mapTile.setRegion(prevRegion);
            }
        }, 1.5f);
    }

    public void rightClickDown(int row, int col) {
        mainPlayer.onTouchDown(row, col);
        stickToPlayer = true;
    }

    public void registerEntity(Entity entity) {
        entities.add(entity);
    }

    public void touchDown(Vector2 world) {
        float x = world.x;
        float y = world.y;
        for (Entity entity : entities) {
            entity.deselect();
        }
        for (Entity entity : entities) {
            if (entity.isClickInside(x, y)) {
                entity.select();
                mainPlayer.followEntity(entity);
                stickToPlayer = true;
                break;
            }
        }
    }

    public void highlightTile(Vector2 screenToTile) {
        // add out of bounds checks
        Vector2 position = screenToTile.add(1, 1);
        if (position.x < 1) position.x = 1;
        if (position.x > WorldMap.MAP_WIDTH) position.x = WorldMap.MAP_WIDTH;
        if (position.y < 1) position.y = 1;
        if (position.y > WorldMap.MAP_HEIGHT) position.y = WorldMap.MAP_HEIGHT;
        selector.setPosition(position);
    }
}
