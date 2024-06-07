package com.rozhkov.callcenter.exceptions;

import org.springframework.data.crossstore.ChangeSetPersister;

public class ResourcesNotFoundException extends ChangeSetPersister.NotFoundException {
    public ResourcesNotFoundException(String msg) {
        System.out.println(msg);;
    }
}
