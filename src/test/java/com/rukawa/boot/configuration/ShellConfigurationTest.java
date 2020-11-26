package com.rukawa.boot.configuration;

import org.junit.jupiter.api.Test;

class ShellConfigurationTest {

    @Test
    void getShellConfiguration() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShellConfiguration shellConfiguration = ShellConfiguration.getShellConfiguration();
                System.out.println(shellConfiguration.hashCode());
            }).start();
        }
    }
}