package com.rukawa.boot.interfaces;

import com.rukawa.boot.exception.ShutdownException;

/**
 * 系统关闭钩子，处理关机事件
 */
public interface IShutdownHook {

    void store() throws ShutdownException;
}
