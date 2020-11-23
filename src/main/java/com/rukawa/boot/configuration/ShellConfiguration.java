package com.rukawa.boot.configuration;

import lombok.Data;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class ShellConfiguration {

    private String windows;

    private String linux;

    private static ShellConfiguration shellConfiguration;

    private static Lock reentrantLock = new ReentrantLock();

    private static final String shellWindows = "reboot.bat";

    private static final String shellLinux = "reboot.sh";

    private ShellConfiguration() {

    }

    static {
        shellConfiguration = new ShellConfiguration();
        String userDir = System.getProperty("user.dir");
        shellConfiguration.setWindows(userDir + File.separator + shellWindows);
        shellConfiguration.setLinux(userDir + File.separator + shellLinux);
    }

    public static ShellConfiguration getShellConfiguration() {
        if (Objects.isNull(shellConfiguration)) {
            reentrantLock.lock();
            if (Objects.isNull(shellConfiguration)) {
                shellConfiguration = new ShellConfiguration();
            }
            reentrantLock.unlock();
        }
        return shellConfiguration;
    }
}
