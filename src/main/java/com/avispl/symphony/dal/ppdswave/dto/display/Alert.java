/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.ppdswave.dto.display;

import java.util.Date;

/**
 * Display alert entry
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class Alert {
    private Date createdAt;
    private String id;
    private String message;

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
     * Retrieves {@link #message}
     *
     * @return value of {@link #message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets {@link #message} value
     *
     * @param message new value of {@link #message}
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
