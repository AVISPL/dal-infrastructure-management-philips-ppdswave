/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display.power;

/**
 * Time blocks of power schedules
 *
 * @author Maksym.Rossiytsev
 * Created on 10/07/2023
 * @since 1.0.0
 * */
public class TimeBlock {
    private String day;
    private String end;
    private String start;

    /**
     * Retrieves {@link #day}
     *
     * @return value of {@link #day}
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets {@link #day} value
     *
     * @param day new value of {@link #day}
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Retrieves {@link #end}
     *
     * @return value of {@link #end}
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets {@link #end} value
     *
     * @param end new value of {@link #end}
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Retrieves {@link #start}
     *
     * @return value of {@link #start}
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets {@link #start} value
     *
     * @param start new value of {@link #start}
     */
    public void setStart(String start) {
        this.start = start;
    }
}
