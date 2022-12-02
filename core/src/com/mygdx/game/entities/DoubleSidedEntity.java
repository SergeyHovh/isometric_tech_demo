package com.mygdx.game.entities;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.map.PathFinder;

public class DoubleSidedEntity extends Entity {
    protected GraphPath<PathFinder.Node> path;
    protected int currentPathIndex = 0;

    public DoubleSidedEntity(int id) {
        super(id, id + 1);
    }

    @Override
    public void move(int row, int col) {
        super.move(row, col);
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
        super.render(batch, delta);
        if (path == null) return;
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

    public void followEntity(Entity entity) {
        findPathAndMove(entity.getTileX(), entity.getTileY());
    }

    public void findPathAndMove(int toX, int toY) {
        findPathAndMove(getTileX(), getTileY(), toX, toY);
    }

    protected void findPathAndMove(int fromX, int fromY, int toX, int toY) {
        path = PathFinder.getPath(new Vector2(fromX, fromY), new Vector2(toX, toY));
        currentPathIndex = 0;
        chooseNextDestination();
    }
}
