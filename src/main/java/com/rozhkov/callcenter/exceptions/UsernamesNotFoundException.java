package com.rozhkov.callcenter.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsernamesNotFoundException extends UsernameNotFoundException {
    public UsernamesNotFoundException(String msg) {
        super(msg);
    }
}
