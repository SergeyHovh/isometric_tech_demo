package com.mygdx.game.entities;

public class EntityFactory {
    public static DoubleSidedEntity SPEAR_ENEMY() {
        DoubleSidedEntity spearman = new DoubleSidedEntity(2);
        spearman.setSpeed(10);
        return spearman;
    }

    public static DoubleSidedEntity ARCHER_ENEMY() {
        DoubleSidedEntity archer = new DoubleSidedEntity(4);
        archer.setSpeed(20);
        return archer;
    }

    public static DoubleSidedEntity THIEF_ENEMY() {
        DoubleSidedEntity thief = new DoubleSidedEntity(6);
        thief.setSpeed(25);
        return thief;
    }

    public static DoubleSidedEntity BAT() {
        DoubleSidedEntity bat = new DoubleSidedEntity(28);
        bat.setSpeed(15);
        return bat;
    }
}
