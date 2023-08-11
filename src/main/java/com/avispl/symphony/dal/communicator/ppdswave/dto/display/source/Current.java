/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display.source;

/**
 * Current input source value placeholder
 *
 * @author Maksym.Rossiytsev
 * Created on 10/08/2023
 * @since 1.0.0
 * */
public class Current {
    private Source reported;

    /**
     * Retrieves {@link #reported}
     *
     * @return value of {@link #reported}
     */
    public Source getReported() {
        return reported;
    }

    /**
     * Sets {@link #reported} value
     *
     * @param reported new value of {@link #reported}
     */
    public void setReported(Source reported) {
        this.reported = reported;
    }
}
