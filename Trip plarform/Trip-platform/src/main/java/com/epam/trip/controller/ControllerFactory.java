package com.epam.trip.controller;

import com.epam.trip.controller.impl.ControllerImpl;
import com.epam.trip.view.View;

public class ControllerFactory {
    private static ControllerImpl controller;

    public static Controller getController(View view) {
        if (controller == null) {
            controller = new ControllerImpl(view);
        }
        return controller;
    }
}