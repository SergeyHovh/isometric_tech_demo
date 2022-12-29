package com.mygdx.game.events.management;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.mygdx.game.events.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventManager {
    private static EventManager instance;

    public ObjectMap<Class<? extends Event>, Pool<Event>> eventPools = new ObjectMap<>();
    public ObjectMap<Class<? extends Event>, Array<EventRunner>> invocationMap = new ObjectMap<>();
    public ObjectMap<Observer, ObjectMap<Class<? extends Event>, EventRunner>> observerInvocationMap = new ObjectMap<>();

    public Array<Event> postponedEventsList = new Array<>();
    public Array<Observer> unregisterList = new Array<>();
    public boolean fireInProgress = false;

    private EventManager() {
        addPool(MouseMovedEvent.class);
        addPool(MapClickEvent.class);
        addPool(MouseDraggedEvent.class);
        addPool(PathGraphChangeEvent.class);
    }

    public static void quickFire(Class<? extends Event> clazz) {
        getInstance().fireEvent(instance.obtainEvent(clazz));
    }

    public interface EventRunner {
        void runEvent(Event event);
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    private void addPool(final Class<? extends Event> clazz) {
        Pool<Event> pool = new Pool<Event>() {
            @Override
            protected Event newObject() {
                try {
                    return ClassReflection.newInstance(clazz);
                } catch (ReflectionException e) {
                    throw new GdxRuntimeException(e);
                }
            }
        };

        eventPools.put(clazz, pool);
    }

    public void unregisterObserver(final Observer observer) {
        if (fireInProgress) {
            unregisterList.add(observer);
        } else {
            unregisterObserverInternal(observer);
        }
    }

    private void unregisterObserverInternal(final Observer observer) {
        for (Class<? extends Event> event : observerInvocationMap.get(observer).keys()) {
            EventRunner eventRunner = observerInvocationMap.get(observer).get(event);
            invocationMap.get(event).removeValue(eventRunner, true);
        }
        observerInvocationMap.remove(observer);
    }

    public void registerObserver(final Observer observer) {
        Method[] declaredMethods = observer.getClass().getMethods();

        for (final Method method : declaredMethods) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);

            if (eventHandler == null) continue;

            //Events should only have one param, and that param should be of instance Event
            if (method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                Class<? extends Event> event = method.getParameterTypes()[0].asSubclass(Event.class);

                if (observer.getClass().isAnonymousClass()) {
                    AccessibleObject.setAccessible(new Method[]{method}, true);
                }

                EventRunner eventRunner = event1 -> {
                    try {
                        method.invoke(observer, event1);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };

                if (!invocationMap.containsKey(event)) {
                    invocationMap.put(event, new Array<EventRunner>());
                }
                if (!observerInvocationMap.containsKey(observer)) {
                    observerInvocationMap.put(observer, new ObjectMap<Class<? extends Event>, EventRunner>());
                }

                Array<EventRunner> observerMethods = invocationMap.get(event);
                observerMethods.add(eventRunner);

                observerInvocationMap.get(observer).put(event, eventRunner);
            }
        }
    }

    public void fireEvent(Event event) {
        if (fireInProgress) {
            postponedEventsList.add(event);
        } else {
            fireInProgress = true;
            if (invocationMap.containsKey(event.getClass())) {
                for (EventRunner eventRunner : invocationMap.get(event.getClass()).toArray(EventRunner.class)) {
                    eventRunner.runEvent(event);
                }
            }
            fireInProgress = false;
            eventPools.get(event.getClass()).free(event);

            //fire postponed events
            postponedEventsList.removeValue(event, false);
            firePostponedEvents();

            // unregister events
            if (unregisterList.size > 0) {
                for (Observer observer : unregisterList.toArray(Observer.class)) {
                    unregisterObserverInternal(observer);
                }
                unregisterList.clear();
            }
        }
    }

    private void firePostponedEvents() {
        for (int i = 0; i < postponedEventsList.size; i++) {
            fireEvent(postponedEventsList.get(i));
        }
    }

    public <T extends Event> T obtainEvent(Class<T> clazz) {
        return (T) eventPools.get(clazz).obtain();
    }

    public void dispose() {
        eventPools.clear();

        for (Array<EventRunner> value : invocationMap.values()) {
            value.clear();
        }
        invocationMap.clear();

        for (ObjectMap<Class<? extends Event>, EventRunner> value : observerInvocationMap.values()) {
            value.clear();
        }
        observerInvocationMap.clear();

        postponedEventsList.clear();
        unregisterList.clear();

        instance = null;
    }
}
