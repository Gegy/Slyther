package net.gegy1000.slyther.client.controller;

import net.gegy1000.slyther.client.SlytherClient;

public interface IController {
    void update(SlytherClient client);

    float getTargetAngle();
    boolean shouldAccelerate();
}