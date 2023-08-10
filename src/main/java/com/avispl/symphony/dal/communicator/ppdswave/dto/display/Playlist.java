/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display;

/**
 * General playlist item information
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class Playlist {
    private String id;
    private String title;

    /**
     * Retrieves {@link #id}
     *
     * @return value of {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets {@link #id} value
     *
     * @param id new value of {@link #id}
     */
    public void setId(String id) {
        this.id = id;
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
}
