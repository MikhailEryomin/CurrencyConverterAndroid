package com.eremix.app;

import java.io.Serializable;

public class Setting implements Serializable {

    private int groupListId;
    private String value;

    public Setting(int groupListId, String value) {
        this.groupListId = groupListId;
        this.value = value;
    }

    public int getGroupListId() {
        return groupListId;
    }

    public String getValue() {
        return value;
    }
}
