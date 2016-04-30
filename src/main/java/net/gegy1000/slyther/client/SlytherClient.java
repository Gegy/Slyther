package net.gegy1000.slyther.client;

import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.game.Snake;

import java.util.ArrayList;
import java.util.List;

public class SlytherClient {
    public int GAME_RADIUS;
    public short MSCPS;
    public short SECTOR_SIZE;
    public short SECTORS_ALONG_EDGE;
    public float SPANG_DV;
    public float NODE_SPEED_1;
    public float NODE_SPEED_2;
    public float NODE_SPEED_3;
    public float MAMU;
    public float MANU2;
    public float CST;
    public byte PROTOCOL_VERSION;

    private ClientNetworkManager networkManager;

    private List<Snake> snakes = new ArrayList<>();
    private List<Food> food = new ArrayList<>();

    public SlytherClient() throws Exception {
        while((this.networkManager = ClientNetworkManager.create(this)) == null);
    }

    public void setup(int gameRadius, short msCPS, short sectorSize, short sectorCountAlongEdge, float spangDV, float nodeSpeed1, float nodeSpeed2, float nodeSpeed3, float mamu, float manu2, float cst, byte protocolVersion) {
        this.GAME_RADIUS = gameRadius;
        this.MSCPS = msCPS;
        this.SECTOR_SIZE = sectorSize;
        this.SECTORS_ALONG_EDGE = sectorCountAlongEdge;
        this.SPANG_DV = spangDV;
        this.NODE_SPEED_1 = nodeSpeed1;
        this.NODE_SPEED_2 = nodeSpeed2;
        this.NODE_SPEED_3 = nodeSpeed3;
        this.MAMU = mamu;
        this.MANU2 = manu2;
        this.CST = cst;
        this.PROTOCOL_VERSION = protocolVersion;

        if (PROTOCOL_VERSION < 6) {
            throw new RuntimeException("Unsupported protocol version (" + PROTOCOL_VERSION + ")" + "!");
        }
    }

    public void addSnake(Snake snake) {
        if (!this.snakes.contains(snake)) {
            this.snakes.add(snake);
        }
        System.out.println("Added snake \"" + snake.getName() + "\" (" + snake.getId() + ") at " + snake.getX() + ", " + snake.getY() + " with skin " + snake.getSkin());
    }

    public void addFood(Food food) {
        if (!this.food.contains(food)) {
            this.food.add(food);
        }
        System.out.println("Added food at " + food.getX() + ", " + food.getY() + " (" + food.getId() + ") with size " + food.getSize() + " and color " + food.getColor());
    }

    public Snake getSnake(short id) {
        for (Snake snake : snakes) {
            if (snake.getId() == id) {
                return snake;
            }
        }
        return null;
    }

    public Food getFood(int id) {
        for (Food food : this.food) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    public void removeFood(int id) {
        this.food.remove(this.getFood(id));
    }
}
