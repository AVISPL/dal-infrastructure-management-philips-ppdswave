/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.concurrent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

 /**
  * Lock to grant operations mutual exclusion based on the deviceId
  *
  * @author Maksym.Rossiytsev
  * Created on 22/08/2023
  * @since 1.0.0
 */
public class DeviceLock {
    private static Set<String> usedKeys= ConcurrentHashMap.newKeySet();

    public boolean tryLock(String key) {
        return usedKeys.add(key);
    }

    public void unlock(String key) {
        usedKeys.remove(key);
    }
}
