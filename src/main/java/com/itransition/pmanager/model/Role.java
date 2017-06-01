package com.itransition.pmanager.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Lenovo on 01.06.2017.
 */
public enum Role implements GrantedAuthority{
    ROLE_ADMIN, ROLE_Manager, ROLE_Dev;

    @Override
    public String getAuthority(){
        return name();
    }
}
