/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto;

import com.avispl.symphony.dal.communicator.ppdswave.dto.display.Display;
import com.avispl.symphony.dal.communicator.ppdswave.dto.display.Playlist;

import java.util.List;

/**
 * Displays response wrapper
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class CustomerByHandle {
    private List<Playlist> playlists;
    private List<Display> displays;

    /**
     * Retrieves {@link #displays}
     *
     * @return value of {@link #displays}
     */
    public List<Display> getDisplays() {
        return displays;
    }

    /**
     * Sets {@link #displays} value
     *
     * @param displays new value of {@link #displays}
     */
    public void setDisplays(List<Display> displays) {
        this.displays = displays;
    }

    /**
     * Retrieves {@link #playlists}
     *
     * @return value of {@link #playlists}
     */
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Sets {@link #playlists} value
     *
     * @param playlists new value of {@link #playlists}
     */
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
