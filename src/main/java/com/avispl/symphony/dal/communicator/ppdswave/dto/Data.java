/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.ppdswave.dto;

/**
 * Response data container
 *
 * @author Maksym.Rossiytsev
 * Created on 10/07/2023
 * @since 1.0.0
 * */
public class Data {
  private CustomerByHandle customerByHandle;

    /**
     * Retrieves {@link #customerByHandle}
     *
     * @return value of {@link #customerByHandle}
     */
    public CustomerByHandle getCustomerByHandle() {
        return customerByHandle;
    }

    /**
     * Sets {@link #customerByHandle} value
     *
     * @param customerByHandle new value of {@link #customerByHandle}
     */
    public void setCustomerByHandle(CustomerByHandle customerByHandle) {
        this.customerByHandle = customerByHandle;
    }
}
