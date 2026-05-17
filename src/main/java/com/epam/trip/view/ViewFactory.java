package com.epam.trip.view;

import com.epam.trip.view.impl.ConsoleView;

public class ViewFactory {
    private static View view;

    public static View getView() {
        if (view == null) {
            view = new ConsoleView();
        }
        return view;
    }
}