package com.rukawa.boot.configuration;

import com.rukawa.boot.interfaces.IShutdownHook;
import com.rukawa.common.util.BeanUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ToString
public class ShellConfiguration {

    @Getter
    @Setter
    private String windows;

    @Getter
    @Setter
    private String linux;

    @Getter
    private Set<IShutdownHook> shutdownHooks = new HashSet<>();

    private static ShellConfiguration shellConfiguration;

    private static Lock reentrantLock = new ReentrantLock();

    private static final String shellWindows = "reboot.bat";

    private static final String shellLinux = "reboot.sh";

    private static final List<String> shellFiles = new ArrayList<>();

    private ShellConfiguration() {
    }

    private static void init() {
        shellFiles.add("shell.properties");
        shellFiles.add("config/shell.properties");
        String serverPath = System.getProperty("user.dir") + "/";
        File shellFile = null;
        for (String file : shellFiles) {
            String fileName = serverPath + file;
            shellFile = new File(fileName);
            if (!shellFile.exists()) {
                shellFile = null;
            } else {
                break;
            }
        }
        shellConfiguration = new ShellConfiguration();
        // require shell params configuration
        ShellParamsConfiguration shellParamsConfiguration = ShellParamsConfiguration.getShellParamsConfiguration();
        shellConfiguration.setShutdownHooks(shellParamsConfiguration);
        if (BeanUtil.isEmpty(shellFile)) {
            shellConfiguration.setWindows(serverPath + shellWindows);
            shellConfiguration.setLinux(serverPath + shellLinux);
        } else {
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(shellFile)) {
                properties.load(inputStream);
                shellConfiguration.setWindows(serverPath + properties.getProperty("windows"));
                shellConfiguration.setLinux(serverPath + properties.getProperty("linux"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setShutdownHooks(Set<IShutdownHook> shutdownHooks) {
        shellConfiguration.shutdownHooks.addAll(shutdownHooks);
    }

    public void setShutdownHooks(IShutdownHook shutdownHook) {
        shellConfiguration.shutdownHooks.add(shutdownHook);
    }

    public static ShellConfiguration getShellConfiguration() {
        if (Objects.isNull(shellConfiguration)) {
            reentrantLock.lock();
            if (Objects.isNull(shellConfiguration)) {
                init();
            }
            reentrantLock.unlock();
        }
        return shellConfiguration;
    }
}
