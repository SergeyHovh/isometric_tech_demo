package com.mygdx.game.entities;

public class GameActorFactory {
    public static AnimatedGameActor SPEAR_ENEMY() {
        AnimatedGameActor spearman = new AnimatedGameActor(2);
        spearman.setSpeed(10);
        return spearman;
    }

    public static AnimatedGameActor ARCHER_ENEMY() {
        AnimatedGameActor archer = new AnimatedGameActor(4);
        archer.setSpeed(20);
        return archer;
    }

    public static AnimatedGameActor THIEF_ENEMY() {
        AnimatedGameActor thief = new AnimatedGameActor(6);
        thief.setSpeed(25);
        return thief;
    }

    public static AnimatedGameActor BAT() {
        AnimatedGameActor bat = new AnimatedGameActor(28);
        bat.setSpeed(15);
        return bat;
    }
}
