package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

public class ColorUtil {
    public static void copyColor(Color color1, Color color2){
        color1.r =  color2.r;
        color1.g =  color2.g;
        color1.b =  color2.b;
        color1.a =  color2.a;
    }
}
