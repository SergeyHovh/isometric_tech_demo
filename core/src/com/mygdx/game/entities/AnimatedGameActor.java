package com.mygdx.game.entities;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.events.PathGraphChangeEvent;
import com.mygdx.game.events.management.EventHandler;
import com.mygdx.game.events.management.EventManager;
import com.mygdx.game.events.management.Observer;
import com.mygdx.game.util.ColorUtil;
import com.mygdx.game.world.map.PathFinder;

public class AnimatedGameActor extends GameActor implements Observer {
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
    private int toX = -1;
    private int toY = -1;
    private GameActor actorToFollow = null;

    public AnimatedGameActor(int id) {
        super(id, id + 1);
        EventManager.getInstance().registerObserver(this);
    }

    @Override
    public void move(int row, int col) {
        super.move(row, col);
        turnTowards(row, col);
    }

    protected void turnTowards(int row, int col) {
        if (row > 0) {
            scaleX = 1;
        } else if (row < 0) {
            scaleX = -1;
        }
        if (col > 0) {
            changeFrameSet(1);
        } else if (col < 0) {
            changeFrameSet(0);
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
        }
        if (path == null && isNear(attackEntity)) {
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
        if (Vector2.dst(x, y, targetX, targetY) <= 1) {
            chooseNextDestination();
        }
    }

    private boolean isNear(AnimatedGameActor attackEntity) {
        if (attackEntity == null) {
            return false;
        }
        // calculate tile distance
        int dx = Math.abs(attackEntity.getTileX() - getTileX());
        int dy = Math.abs(attackEntity.getTileY() - getTileY());
        return dx <= 1 && dy <= 1;
    }

    private void chooseNextDestination() {
        if (path == null) return;
        if (currentPathIndex + 1 > path.getCount() - 1) {
            actorToFollow = null;
            path = null;
            toX = -1;
            toY = -1;
            return;
        }
        PathFinder.Node current = path.get(currentPathIndex);
        PathFinder.Node next = path.get(++currentPathIndex);
        int moveX = (int) (next.tilePos.x - current.tilePos.x);
        int moveY = (int) (next.tilePos.y - current.tilePos.y);
        move(moveX, moveY);
    }

    public void followGameActor(GameActor gameActor) {
        actorToFollow = gameActor;
        findPathAndMove(gameActor.getTileX(), gameActor.getTileY());
    }

    public void findPathAndMove(int toX, int toY) {
        this.toX = toX;
        this.toY = toY;
        GameActor gameActor = MyGdxGame.API().getWorld().containsGameActor(toX, toY);
        if (gameActor != null) {
            // find the closest adjacent tile other than the selected one
            int closestX = 0;
            int closestY = 0;
            float closestDistance = Float.MAX_VALUE;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) continue;
                    GameActor actorOnNewPos = MyGdxGame.API().getWorld().containsGameActor(toX + x, toY + y);
                    if (!MyGdxGame.API().getWorld().isPassable(toX + x, toY + y)) continue;
                    if (actorOnNewPos != null && actorOnNewPos != this) continue;
                    float distance = Vector2.dst(getTileX(), getTileY(), toX + x, toY + y);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestX = x;
                        closestY = y;
                    }
                }
            }
            this.toX += closestX;
            this.toY += closestY;
        }
        findPathAndMove(getTileX(), getTileY(), this.toX, this.toY);
    }

    @EventHandler
    public void onPathGraphChangeEvent(PathGraphChangeEvent event) {
        updatePathfinding();
    }

    private void updatePathfinding() {
        if (actorToFollow != null) {
            followGameActor(actorToFollow);
            return;
        }
        if (toX != -1 && toY != -1) {
            findPathAndMove(toX, toY);
            return;
        }
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
