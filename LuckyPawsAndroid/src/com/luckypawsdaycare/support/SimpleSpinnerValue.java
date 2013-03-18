/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.support;

public class SimpleSpinnerValue {
    String name;
    boolean selected;

    public SimpleSpinnerValue(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setIsSelected(boolean selected) {
        this.selected = selected;
    }
}
