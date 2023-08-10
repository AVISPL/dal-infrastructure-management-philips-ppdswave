/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto.display.source;

import com.avispl.symphony.dal.communicator.ppdswave.Constants;
import com.avispl.symphony.dal.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Content Source node, containing information about available and current source values
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class ContentSource {
    private List<Source> available;
    private Current current;

    /**
     * Retrieves {@link #available}, omitting the blank values, since PPDS Wave API may include blank values
     *
     * @return value of {@link #available}
     */
    public List<Source> getAvailable() {
        return available.stream().filter(source -> source != null &&
                StringUtils.isNotNullOrEmpty(source.getSource())).collect(Collectors.toList());
    }

    /**
     * Sets {@link #available} value
     *
     * @param available new value of {@link #available}
     */
    public void setAvailable(List<Source> available) {
        this.available = available;
    }

    /**
     * Retrieves {@link #current} value, sets "Empty" if there's no valid value present.
     *
     * @return String value of {@link #current} content source
     */
    public String getCurrent() {
        if (current == null) {
            return Constants.Utility.EMPTY;
        }
        Source reportedSource = current.getReported();
        if (reportedSource == null) {
            return Constants.Utility.EMPTY;
        }
        String sourceValue = reportedSource.getSource();
        return StringUtils.isNullOrEmpty(sourceValue) ? Constants.Utility.EMPTY : sourceValue;
    }

    /**
     * Sets {@link #current} value
     *
     * @param current new value of {@link #current}
     */
    public void setCurrent(Current current) {
        this.current = current;
    }
}
