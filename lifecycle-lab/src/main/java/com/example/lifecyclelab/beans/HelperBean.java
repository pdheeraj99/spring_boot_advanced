package com.example.lifecyclelab.beans;

import org.springframework.stereotype.Component;

@Component
public class HelperBean {
    public HelperBean() {
        System.out.println("HelperBean: constructor");
    }
}
