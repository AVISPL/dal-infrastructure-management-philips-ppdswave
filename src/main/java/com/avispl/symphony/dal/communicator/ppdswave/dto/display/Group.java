/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display;

/**
 * Display group wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class Group {
    private String name;

    /**
     * Retrieves {@link #name}
     *
     * @return value of {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets {@link #name} value
     *
     * @param name new value of {@link #name}
     */
    public void setName(String name) {
        this.name = name;
    }
}
