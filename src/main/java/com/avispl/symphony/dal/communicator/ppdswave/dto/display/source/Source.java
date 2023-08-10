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
    private String applicationId;
    private String label;
    private String playlistId;
    private String index;

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

    /**
     * Retrieves {@link #applicationId}
     *
     * @return value of {@link #applicationId}
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets {@link #applicationId} value
     *
     * @param applicationId new value of {@link #applicationId}
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Retrieves {@link #label}
     *
     * @return value of {@link #label}
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets {@link #label} value
     *
     * @param label new value of {@link #label}
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Retrieves {@link #playlistId}
     *
     * @return value of {@link #playlistId}
     */
    public String getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets {@link #playlistId} value
     *
     * @param playlistId new value of {@link #playlistId}
     */
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Retrieves {@link #index}
     *
     * @return value of {@link #index}
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets {@link #index} value
     *
     * @param index new value of {@link #index}
     */
    public void setIndex(String index) {
        this.index = index;
    }
}
