/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display.power;

/**
 * Power schedule wrapper
 *
 * @author Maksym.Rossiytsev
 * Created on 10/07/2023
 * @since 1.0.0
 * */
public class PowerSchedule {
    private boolean isSynced;
    private LatestJob latestJob;
    private Schedule schedule;

    /**
     * Retrieves {@link #isSynced}
     *
     * @return value of {@link #isSynced}
     */
    public boolean getIsSynced() {
        return isSynced;
    }

    /**
     * Sets {@link #isSynced} value
     *
     * @param isSynced new value of {@link #isSynced}
     */
    public void setIsSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }

    /**
     * Retrieves {@link #latestJob}
     *
     * @return value of {@link #latestJob}
     */
    public LatestJob getLatestJob() {
        return latestJob;
    }

    /**
     * Sets {@link #latestJob} value
     *
     * @param latestJob new value of {@link #latestJob}
     */
    public void setLatestJob(LatestJob latestJob) {
        this.latestJob = latestJob;
    }

    /**
     * Retrieves {@link #schedule}
     *
     * @return value of {@link #schedule}
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Sets {@link #schedule} value
     *
     * @param schedule new value of {@link #schedule}
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
