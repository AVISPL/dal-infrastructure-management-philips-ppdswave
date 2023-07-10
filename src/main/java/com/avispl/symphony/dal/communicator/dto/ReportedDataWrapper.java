/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.dto;

import java.util.List;

/**
 * Reported data response wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class ReportedDataWrapper {
    private List<String> reported;

    /**
     * Retrieves {@link #reported}
     *
     * @return value of {@link #reported}
     */
    public List<String> getReported() {
        return reported;
    }

    /**
     * Sets {@link #reported} value
     *
     * @param reported new value of {@link #reported}
     */
    public void setReported(List<String> reported) {
        this.reported = reported;
    }
}
