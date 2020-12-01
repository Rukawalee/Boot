package com.rukawa.boot.configuration;

import com.rukawa.boot.exception.ShutdownException;
import com.rukawa.boot.interfaces.IShutdownHook;
import com.rukawa.common.util.BeanUtil;
import com.rukawa.common.util.FileUtil;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@ToString
public class ShellParamsConfiguration implements Serializable, IShutdownHook {

    private Object[] params;

    private static ShellParamsConfiguration shellParamsConfiguration;

    private static final String bootFile = "bootParams";

    private static final Lock lock = new ReentrantLock();

    private ShellParamsConfiguration() {
    }

    private static void init() {
        Optional<ShellParamsConfiguration> shellParamsConfigurationOptional = FileUtil.readObject(ShellParamsConfiguration.class, bootFile);
        if (shellParamsConfigurationOptional.isPresent()) {
            shellParamsConfiguration = shellParamsConfigurationOptional.get();
        } else {
            shellParamsConfiguration = new ShellParamsConfiguration();
        }
    }

    public static ShellParamsConfiguration getShellParamsConfiguration() {
        if (BeanUtil.isEmpty(shellParamsConfiguration)) {
            lock.lock();
            if (BeanUtil.isEmpty(shellParamsConfiguration)) {
                init();
            }
            lock.unlock();
        }
        return shellParamsConfiguration;
    }

    @Override
    public void store() throws ShutdownException {
        FileUtil.writeObject(shellParamsConfiguration, bootFile);
    }
}
