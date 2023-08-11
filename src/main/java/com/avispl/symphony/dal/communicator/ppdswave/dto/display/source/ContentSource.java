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
 * Created on 10/08/2023
 * @since 1.0.0
 * */
public class ContentSource {
    public class SourceType {
        public SourceType(String type, String value) {
            this.type = type;
            this.value = value;
        }

        private String type;
        private String value;

        /**
         * Retrieves {@link #type}
         *
         * @return value of {@link #type}
         */
        public String getType() {
            return type;
        }

        /**
         * Sets {@link #type} value
         *
         * @param type new value of {@link #type}
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Retrieves {@link #value}
         *
         * @return value of {@link #value}
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets {@link #value} value
         *
         * @param value new value of {@link #value}
         */
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s:%s", type, value);
        }
    }

    private List<Source> available;
    private Current current;

    /**
     * Retrieves {@link #available}, by source, omitting the blank values, since PPDS Wave API may include blank values
     *
     * @return value of {@link #available}
     */
    public List<Source> getAvailableInputSources() {
        return available.stream().filter(source -> source != null &&
                StringUtils.isNotNullOrEmpty(source.getSource())).collect(Collectors.toList());
    }

    /**
     * Retrieves {@link #available}, by applicationId omitting the blank values, since PPDS Wave API may include blank values
     *
     * @return value of {@link #available}
     */
    public List<Source> getAvailableAppContentSources() {
        return available.stream().filter(source -> source != null &&
                StringUtils.isNotNullOrEmpty(source.getApplicationId())).collect(Collectors.toList());
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
    public SourceType getCurrent() {
        SourceType defaultType = new SourceType(Constants.Utility.EMPTY, Constants.Utility.EMPTY);
        if (current == null) {
            return defaultType;
        }
        Source reportedSource = current.getReported();
        if (reportedSource == null) {
            return defaultType;
        }
        String sourceValue = reportedSource.getSource();
        String applicationId = reportedSource.getApplicationId();
        String playlistId = reportedSource.getPlaylistId();
        String bookmarkIndex = reportedSource.getIndex();

        if (StringUtils.isNotNullOrEmpty(sourceValue)) {
            return new SourceType(Constants.SourceType.INPUT, sourceValue);
        }
        if (StringUtils.isNotNullOrEmpty(applicationId)) {
            return new SourceType(Constants.SourceType.APPLICATION, applicationId);
        }
        if (StringUtils.isNotNullOrEmpty(playlistId)) {
            return new SourceType(Constants.SourceType.PLAYLIST, playlistId);
        }
        if (StringUtils.isNotNullOrEmpty(bookmarkIndex)) {
            return new SourceType(Constants.SourceType.BOOKMARK, bookmarkIndex);
        }
        return defaultType;
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
