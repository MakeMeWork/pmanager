package com.itransition.pmanager.model;

/**
 * Created by Lenovo on 01.06.2017.
 */
public enum Role{
    ROLE_USER("Developer"),
    ROLE_MANAGER("Manager"),
    ROLE_ADMIN("Admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
