package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.map.Textures;

public class GameScreen implements Screen {

    public static final int width = 1600;
    public static final int height = 900;

    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 0.3f;

    private final SpriteBatch batch;
    public final OrthographicCamera camera;
    private float time = 1f;
    private boolean stickToPlayer = true;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        Gdx.input.setInputProcessor(new InputProcessing());
        camera = new OrthographicCamera(width, height);
    }

    @Override
    public void show() {
        camera.zoom = 0.2f;
        MyGdxGame.API().getWorld().show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(35 / 255f, 122 / 255f, 148 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        MyGdxGame.API().getWorld().render(batch, delta);
        batch.end();

        time += delta;
        if (time >= 0.5f + delta / 2) {
            time = 0;
            tick();
        }
    }

    private void tick() {
        MyGdxGame.API().getWorld().tick();
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
}
