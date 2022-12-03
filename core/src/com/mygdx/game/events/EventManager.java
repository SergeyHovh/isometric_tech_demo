package com.mygdx.game.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventManager {
    private static final ObjectMap<Class<? extends Event>, Event> eventMap = new ObjectMap<>();
    private static EventManager instance;

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    private EventManager() {
    }

    private final Array<Observer> OBSERVERS = new Array<>();

    public void registerObserver(Observer observer) {
        if (OBSERVERS.contains(observer, false)) return;
        OBSERVERS.add(observer);
    }

    public void removeObserver(Observer observer) {
        if (!OBSERVERS.contains(observer, false)) return;
        OBSERVERS.removeValue(observer, false);
    }

    public <T extends Event> T obtainEvent(Class<T> clazz) {
        try {
            if (!eventMap.containsKey(clazz)) {
                eventMap.put(clazz, clazz.newInstance());
            }
            T t = (T) eventMap.get(clazz);
            t.reset();
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GdxRuntimeException("NO SUCH EVENT", e);
        }
    }

    public <E extends Event> void fireEvent(E event) {
        for (Observer observer : OBSERVERS.toArray(Observer.class)) {
            Gdx.app.postRunnable(() -> {
                notifyObserver(observer, event);
            });
        }
    }

    private <E extends Event> void notifyObserver(Observer observer, E event) {
        Method[] declaredMethods = observer.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            // should have only 1 annotation
            if (declaredAnnotations.length != 1) continue;
            for (Annotation declaredAnnotation : declaredAnnotations) {
                if (!(declaredAnnotation instanceof EventHandler)) continue;
                // should have only 1 parameter
                if (method.getParameterCount() != 1) continue;

                if (method.getParameters()[0].getType().equals(event.getClass())) {
                    try {
                        method.invoke(observer, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
