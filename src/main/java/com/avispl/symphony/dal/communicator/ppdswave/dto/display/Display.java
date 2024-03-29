/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display;

import com.avispl.symphony.dal.communicator.ppdswave.dto.display.power.PowerSchedule;
import com.avispl.symphony.dal.communicator.ppdswave.dto.display.source.ContentSource;

import java.util.List;

/**
 * Display data container
 *
 * @author Maksym.Rossiytsev
 * Created on 10/07/2023
 * @since 1.0.0
 * */
public class Display {
    private String id;
    private Platform platform;
    private PowerSchedule powerSchedule;
    private List<Alert> alerts;
    private List<Group> groups;
    private List<AppSubscription> appSubscriptions;
    private Bookmarks bookmarks;
    private ContentSource contentSource;

    /**
     * Retrieves {@link #platform}
     *
     * @return value of {@link #platform}
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Sets {@link #platform} value
     *
     * @param platform new value of {@link #platform}
     */
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * Retrieves {@link #powerSchedule}
     *
     * @return value of {@link #powerSchedule}
     */
    public PowerSchedule getPowerSchedule() {
        return powerSchedule;
    }

    /**
     * Sets {@link #powerSchedule} value
     *
     * @param powerSchedule new value of {@link #powerSchedule}
     */
    public void setPowerSchedule(PowerSchedule powerSchedule) {
        this.powerSchedule = powerSchedule;
    }

    /**
     * Retrieves {@link #groups}
     *
     * @return value of {@link #groups}
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * Sets {@link #groups} value
     *
     * @param groups new value of {@link #groups}
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Retrieves {@link #alerts}
     *
     * @return value of {@link #alerts}
     */
    public List<Alert> getAlerts() {
        return alerts;
    }

    /**
     * Sets {@link #alerts} value
     *
     * @param alerts new value of {@link #alerts}
     */
    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    /**
     * Retrieves {@link #appSubscriptions}
     *
     * @return value of {@link #appSubscriptions}
     */
    public List<AppSubscription> getAppSubscriptions() {
        return appSubscriptions;
    }

    /**
     * Sets {@link #appSubscriptions} value
     *
     * @param appSubscriptions new value of {@link #appSubscriptions}
     */
    public void setAppSubscriptions(List<AppSubscription> appSubscriptions) {
        this.appSubscriptions = appSubscriptions;
    }

    /**
     * Retrieves {@link #bookmarks}
     *
     * @return value of {@link #bookmarks}
     */
    public Bookmarks getBookmarks() {
        return bookmarks;
    }

    /**
     * Sets {@link #bookmarks} value
     *
     * @param bookmarks new value of {@link #bookmarks}
     */
    public void setBookmarks(Bookmarks bookmarks) {
        this.bookmarks = bookmarks;
    }

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
     * Retrieves {@link #contentSource}
     *
     * @return value of {@link #contentSource}
     */
    public ContentSource getContentSource() {
        return contentSource;
    }

    /**
     * Sets {@link #contentSource} value
     *
     * @param contentSource new value of {@link #contentSource}
     */
    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }
}
