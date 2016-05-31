package net.gegy1000.slyther.game;

import net.gegy1000.slyther.game.entity.*;
import net.gegy1000.slyther.network.NetworkManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Game<NET extends NetworkManager, CFG extends Configuration, SNK extends Snake<?>, SCT extends Sector<?>, FD extends Food<?>, PRY extends Prey<?>> {
    private List<Entity> entities = new ArrayList<>();
    private List<SNK> snakes = new ArrayList<>();
    private List<SCT> sectors = new ArrayList<>();
    private List<FD> foods = new ArrayList<>();
    private List<PRY> preys = new ArrayList<>();

    public List<LeaderboardEntry> leaderboard = new ArrayList<>();
    public boolean[][] map = new boolean[80][80];

    public NET networkManager;

    public CFG configuration;

    private Queue<FutureTask<?>> tasks = new LinkedBlockingDeque<>();

    public void scheduleTask(Callable<?> callable) {
        tasks.add(new FutureTask<>(callable));
    }

    protected final void runTasks() {
        while (tasks.size() > 0) {
            FutureTask<?> task = tasks.poll();
            try {
                task.run();
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addSector(SCT sector) {
        if (!sectors.contains(sector)) {
            sectors.add(sector);
        }
    }

    public void removeSector(SCT sector) {
        sectors.remove(sector);
    }

    public void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
            if (entity instanceof Snake) {
                snakes.add((SNK) entity);
            } else if (entity instanceof Food) {
                foods.add((FD) entity);
            } else if (entity instanceof Prey) {
                preys.add((PRY) entity);
            }
        }
    }

    public void removeEntity(Entity entity) {
        if (entities.remove(entity)) {
            if (entity instanceof Snake) {
                snakes.remove(entity);
            } else if (entity instanceof Food) {
                foods.remove(entity);
            } else if (entity instanceof Prey) {
                preys.remove(entity);
            }
        }
    }

    public Iterator<Entity> entityIterator() {
        return new Iterator<Entity>() {
            private int index;

            private Entity lastEntity;

            @Override
            public boolean hasNext() {
                return index < entities.size();
            }

            @Override
            public Entity next() {
                return lastEntity = entities.get(index++);
            }

            @Override
            public void remove() {
                if (lastEntity == null) {
                    throw new IllegalStateException();
                }
                removeEntity(lastEntity);
                index--;
            }
        };
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<SNK> getSnakes() {
        return snakes;
    }

    public List<FD> getFoods() {
        return foods;
    }

    public List<SCT> getSectors() {
        return sectors;
    }

    public List<PRY> getPreys() {
        return preys;
    }

    public abstract int getGameRadius();
    public abstract int getMSCPS();
    public abstract int getSectorSize();
    public abstract int getSectorsAlongEdge();
    public abstract float getSpangDv();
    public abstract float getNsp1();
    public abstract float getNsp2();
    public abstract float getNsp3();
    public abstract float getMamu();
    public abstract float getMamu2();
    public abstract float getCST();

    public abstract float getFPSL(int sct);
    public abstract float getFMLT(int sct);
}
