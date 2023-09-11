/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.ppdswave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * General response wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class ResponseWrapper {
    @JsonProperty("data")
    private Data data;

    /**
     * Retrieves {@link #data}
     *
     * @return value of {@link #data}
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets {@link #data} value
     *
     * @param data new value of {@link #data}
     */
    public void setData(Data data) {
        this.data = data;
    }
}
