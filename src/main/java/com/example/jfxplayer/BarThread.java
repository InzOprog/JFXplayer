package com.example.jfxplayer;

import javax.swing.*;

public class BarThread extends Thread {
    private static final int DELAY = 500;

    JProgressBar progressBar;

    public BarThread(JProgressBar bar) {
        progressBar = bar;
    }

    public void run() {
        int minimum = progressBar.getMinimum();
        int maximum = progressBar.getMaximum();
        for (int i = minimum; i < maximum; i++) {
            try {
                int value = progressBar.getValue();
                progressBar.setValue(value + 1);

                Thread.sleep(DELAY);
            } catch (InterruptedException ignoredException) {
            }
        }
    }
}
