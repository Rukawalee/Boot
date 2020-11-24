package com.rukawa.boot.configuration;

import com.rukawa.common.util.BeanUtil;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@ToString
public class ShellConfiguration {

    private String windows;

    private String linux;

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
