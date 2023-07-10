/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.dto.display.power;

import java.util.Date;
import java.util.List;

/**
 * Power schedule data
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class Schedule {
    private Date createdAt;
    private String description;
    private String title;
    private List<TimeBlock> timeBlocks;

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

    /**
     * Retrieves {@link #description}
     *
     * @return value of {@link #description}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets {@link #description} value
     *
     * @param description new value of {@link #description}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves {@link #title}
     *
     * @return value of {@link #title}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets {@link #title} value
     *
     * @param title new value of {@link #title}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves {@link #timeBlocks}
     *
     * @return value of {@link #timeBlocks}
     */
    public List<TimeBlock> getTimeBlocks() {
        return timeBlocks;
    }

    /**
     * Sets {@link #timeBlocks} value
     *
     * @param timeBlocks new value of {@link #timeBlocks}
     */
    public void setTimeBlocks(List<TimeBlock> timeBlocks) {
        this.timeBlocks = timeBlocks;
    }
}
