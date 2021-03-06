package com.rukawa.boot.interfaces.impl;

import com.rukawa.boot.configuration.ShellConfiguration;
import com.rukawa.boot.configuration.ShellParamsConfiguration;
import com.rukawa.boot.enumeration.OSEnum;
import com.rukawa.boot.exception.ShutdownException;
import com.rukawa.boot.interfaces.IBoot;
import com.rukawa.boot.interfaces.IShutdownHook;
import com.rukawa.common.util.BeanUtil;
import com.rukawa.common.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BootImpl implements IBoot {

    private ShellConfiguration shellConfiguration = ShellConfiguration.getShellConfiguration();

    private ShellParamsConfiguration shellParamsConfiguration = ShellParamsConfiguration.getShellParamsConfiguration();

    @Override
    public void restartByParams(Object... args) throws FileNotFoundException {
        OSEnum osEnum = OSEnum.getOSEnum(System.getProperty("os.name"));
        if (BeanUtil.isEmpty(args)) {
            args = shellParamsConfiguration.getParams();
        } else {
            shellParamsConfiguration.setParams(args);
        }
        switch (osEnum) {
            case LINUX:
                restart(StringUtil.substringByLastKey(shellConfiguration.getLinux(), ".sh"), args);
                break;
            case WINDOWS:
                String windows = shellConfiguration.getWindows();
                windows = StringUtil.substringByLastKey(windows, ".bat");
                windows = StringUtil.substringByLastKey(windows, ".cmd");
                restart(windows, args);
                break;
        }
    }

    @Override
    public void restart(String shellName, Object... args) throws FileNotFoundException {
        OSEnum osEnum = OSEnum.getOSEnum(System.getProperty("os.name"));
        if (!BeanUtil.isEmpty(shellName)) {
            switch (osEnum) {
                case LINUX:
                    shellConfiguration.setLinux(shellName + ".sh");
                    break;
                case WINDOWS:
                    shellConfiguration.setWindows(shellName + ".bat");
                    break;
            }
        }
        String fileName = "";
        StringBuilder shellCommand = new StringBuilder();
        switch (osEnum) {
            case WINDOWS:
                fileName = shellConfiguration.getWindows();
                shellCommand.append("cmd /c");
                break;
            case LINUX:
                fileName = shellConfiguration.getLinux();
                shellCommand.append("sh ");
                break;
        }
        shellCommand.append(fileName)
                .append(" ");
        if (!BeanUtil.isEmpty(args)) {
            for (Object arg : args) {
                shellCommand.append(arg + " ");
            }
        }
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException(fileName + " not found!");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (IShutdownHook iShutdownHook : shellConfiguration.getShutdownHooks()) {
                try {
                    iShutdownHook.store();
                } catch (ShutdownException e) {
                    e.printStackTrace();
                }
            }
            // sleep 500 ms
            try {
                Thread.sleep(500);
                Runtime.getRuntime().exec(shellCommand.toString().trim());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        System.exit(0);
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
