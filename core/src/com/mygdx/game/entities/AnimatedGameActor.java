package com.mygdx.game.entities;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ColorUtil;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.map.PathFinder;

public class AnimatedGameActor extends GameActor {
    protected GraphPath<PathFinder.Node> path;
    protected int currentPathIndex = 0;
    private Color color = Color.WHITE;
    private final Color tmpColor = new Color();
    private int hp = 10;
    private int damage = 2;
    private float cooldown = 0.4f;
    private float attackCooldownTimer = 0;
    private AnimatedGameActor attackEntity;
    private boolean shouldAttack = false;

    public AnimatedGameActor(int id) {
        super(id, id + 1);
    }

    @Override
    public void move(int row, int col) {
        super.move(row, col);
        turnTowards(row, col);
    }

    private void turnTowards(int row, int col) {
        if (row == 1) {
            scaleX = 1;
        } else if (row == -1) {
            scaleX = -1;
        }
        if (col == 1) {
            changeFrameSet(1);
            scaleX = -1;
        } else if (col == -1) {
            changeFrameSet(0);
            scaleX = 1;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (hp <= 0) {
            setState(State.DEAD);
        }
        ColorUtil.copyColor(tmpColor, batch.getColor());
        batch.setColor(color);
        super.render(batch, delta);
        batch.setColor(tmpColor);
        if (offsetX > 0) {
            offsetX -= delta * 10 * MathUtils.random(1f, 2f);
            if (offsetX < 0) {
                offsetX = 0;
            }
        }
        if (offsetY > 0) {
            offsetY -= delta * 10 * MathUtils.random(1f, 2f);
            if (offsetY < 0) {
                offsetY = 0;
            }
        }

        if (offsetX == 0 && offsetY == 0) {
            setColor(Color.WHITE);
            deselect();
        }
        if (path == null) {
            if (attackEntity == null) {
                shouldAttack = false;
            }
            if (shouldAttack) {
                attackCooldownTimer += delta;
                setState(State.ATTACK);
                if (attackCooldownTimer > cooldown) {
                    attackEntity.takeDamage();
                    attackEntity.hp -= damage;
                    attackCooldownTimer = 0;
                }
                if (attackEntity.hp <= 0) {
                    setState(State.IDLE);
                    attackEntity = null;
                    shouldAttack = false;
                }
            }
            return;
        }
        if (Math.abs(x - targetX) <= 0.1f && Math.abs(y - targetY) <= 0.1f) {
            chooseNextDestination();
        }
    }

    private void chooseNextDestination() {
        if (path == null) return;
        if (currentPathIndex + 1 > path.getCount() - 1) {
            path = null;
            return;
        }
        PathFinder.Node current = path.get(currentPathIndex);
        PathFinder.Node next = path.get(++currentPathIndex);
        int moveX = (int) (next.tilePos.x - current.tilePos.x);
        int moveY = (int) (next.tilePos.y - current.tilePos.y);
        move(moveX, moveY);
    }

    public void followGameActor(GameActor gameActor) {
        findPathAndMove(gameActor.getTileX(), gameActor.getTileY());
    }

    public void findPathAndMove(int toX, int toY) {
        GameActor gameActor = MyGdxGame.API().getWorld().containsGameActor(toX, toY);
        if (gameActor != null) {
            // find the closest adjacent tile other than the selected one
            int closestX = 0;
            int closestY = 0;
            float closestDistance = Float.MAX_VALUE;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) continue;
                    if (!MyGdxGame.API().getWorld().isPassable(toX + x, toY + y)) continue;
                    float distance = Vector2.dst(getTileX(), getTileY(), toX  + x, toY + y);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestX = x;
                        closestY = y;
                    }
                }
            }
            toX += closestX;
            toY += closestY;
        }
        findPathAndMove(getTileX(), getTileY(), toX, toY);
    }

    protected void findPathAndMove(int fromX, int fromY, int toX, int toY) {
        path = PathFinder.getPath(new Vector2(fromX, fromY), new Vector2(toX, toY));
        currentPathIndex = 0;
        chooseNextDestination();
    }

    void takeDamage() {
        // damage animation
        offsetX = MathUtils.random(1f, 2f);
        offsetY = MathUtils.random(1f, 2f);
        setColor(Color.RED);
    }

    void setColor(Color color) {
        this.color = color;
    }

    public void attackEntity(AnimatedGameActor gameActor) {
        if (gameActor == this) return;
        shouldAttack = true;
        attackEntity = gameActor;
        // turn towards the enemy
        if (gameActor == null) return;
        int moveX = gameActor.getTileX() - getTileX();
        int moveY = gameActor.getTileY() - getTileY();
        turnTowards(moveX, moveY);
    }
}
