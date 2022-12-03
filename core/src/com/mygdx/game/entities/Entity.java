package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.CoordinateUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.World;
import com.mygdx.game.map.Textures;
import com.mygdx.game.map.WorldMap;

public class Entity {
    public enum State {
        IDLE(0), ATTACK(1), DEAD(2);

        private final int index;

        State(int index) {
            this.index = index;
        }
    }

    private static final Interpolation moveInterpolation = Interpolation.smooth;
    protected float offsetX, offsetY;
    protected float x, y;
    protected float targetX, targetY;
    private int tileX, tileY;
    private float width, height;
    protected float speed = 1;

    private State state;

    private Animation<TextureRegion>[][] currentAnimationPool;
    private final Animation<TextureRegion>[][] animationPool;
    private final Animation<TextureRegion>[][] highlightedAnimationPool;
    private float stateTime = 0;
    protected float scaleX = 1;
    private int index;

    public Entity(int... id) {
        this(MyGdxGame.API().getWorld(), id);
    }

    public Entity(World world, int... ids) {
        world.registerEntity(this);
        animationPool = new Animation[2][3];
        highlightedAnimationPool = new Animation[2][3];
        TextureRegion deadTexture = Textures.entities[Textures.entities.length - 1][0];

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            TextureRegion[] entityAnimations = Textures.entities[id];
            animationPool[i][State.IDLE.index] = new Animation<>(1 / 4f, entityAnimations[0], entityAnimations[1]);
            animationPool[i][State.ATTACK.index] = new Animation<>(1 / 4f, entityAnimations[2], entityAnimations[3]);
            animationPool[i][State.DEAD.index] = new Animation<>(1 / 4f, deadTexture);
        }

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            TextureRegion[] entityAnimations = Textures.highlightedEntities[id];
            highlightedAnimationPool[i][State.IDLE.index] = new Animation<>(1 / 4f, entityAnimations[0], entityAnimations[1]);
            highlightedAnimationPool[i][State.ATTACK.index] = new Animation<>(1 / 4f, entityAnimations[2], entityAnimations[3]);
            highlightedAnimationPool[i][State.DEAD.index] = new Animation<>(1 / 4f, deadTexture);
        }
        currentAnimationPool = animationPool;

        convertTileToScreen();
        setState(State.IDLE);
    }

    public void changeFrameSet(int index) {
        this.index = index;
        if (index < 0 || index > animationPool.length - 1) {
            Gdx.app.error("Entity#changeFrameSet", "index out of bounds: " + index);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        stateTime += delta; // Accumulate elapsed animation time
        if (x != targetX) {
            x = moveInterpolation.apply(x, targetX, speed * delta);
        }
        if (y != targetY) {
            y = moveInterpolation.apply(y, targetY, speed * delta);
        }
        // Get current frame of animation for the current stateTime
        TextureRegion keyFrame = currentAnimationPool[index][state.index].getKeyFrame(stateTime, true);
        width = keyFrame.getRegionWidth();
        height = keyFrame.getRegionHeight();
        batch.draw(keyFrame,
                x + offsetX, y + offsetX,
                width / 2f, height / 2f,
                width, height,
                scaleX, 1, 0);
    }

    public void move(int row, int col) {
        tileX += row;
        tileY += col;
        if (tileX < 0) {
            tileX = 0;
        }
        if (tileY < 0) {
            tileY = 0;
        }
        if (tileX > WorldMap.MAP_WIDTH - 1) {
            tileX = WorldMap.MAP_WIDTH - 1;
        }
        if (tileY > WorldMap.MAP_HEIGHT - 1) {
            tileY = WorldMap.MAP_HEIGHT - 1;
        }
        convertTileToScreen();
    }

    private void convertTileToScreen() {
        convertTileToScreen(false);
    }

    private void convertTileToScreen(boolean immediate) {
        Vector2 tileToScreen = CoordinateUtils.tileToScreen(tileX, tileY);
        targetX = tileToScreen.x;
        targetY = tileToScreen.y;
        if (immediate) {
            x = targetX;
            y = targetY;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void dispose() {

    }

    public void setPosition(int row, int col) {
        tileX = row;
        tileY = col;
        convertTileToScreen(true);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isClickInside(float x, float y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public void select() {
        currentAnimationPool = highlightedAnimationPool;
    }

    public void deselect() {
        currentAnimationPool = animationPool;
    }
}
