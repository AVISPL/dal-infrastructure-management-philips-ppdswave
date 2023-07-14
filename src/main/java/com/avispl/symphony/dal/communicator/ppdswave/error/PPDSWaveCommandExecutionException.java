/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.error;

/**
 * Exception to use for whenever PPDS Wave API control command fails
 *
 * @author Maksym.Rossiytsev
 * @since 1.0.0
 * */
public class PPDSWaveCommandExecutionException extends Exception{
    public PPDSWaveCommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
