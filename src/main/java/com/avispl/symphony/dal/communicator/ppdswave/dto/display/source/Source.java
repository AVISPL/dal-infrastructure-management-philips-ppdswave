/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display.source;

/**
 * Input source value wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class Source {
    private String source;

    /**
     * Retrieves {@link #source}
     *
     * @return value of {@link #source}
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets {@link #source} value
     *
     * @param source new value of {@link #source}
     */
    public void setSource(String source) {
        this.source = source;
    }
}
