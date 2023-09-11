/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.ppdswave.dto.display.power;

import java.util.Date;

/**
 * Latest power schedule job timestamp wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class LatestJob {
    private Date createdAt;

    /**
     * Retrieves {@link #createdAt}
     *
     * @return value of {@link #createdAt}
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets {@link #createdAt} value
     *
     * @param createdAt new value of {@link #createdAt}
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
