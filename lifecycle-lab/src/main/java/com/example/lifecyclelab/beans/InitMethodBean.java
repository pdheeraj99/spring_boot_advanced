package com.example.lifecyclelab.beans;

public class InitMethodBean {
    private String message;

    public InitMethodBean() {
        System.out.println("InitMethodBean: constructor");
    }

    public void setMessage(String message) {
        this.message = message;
        System.out.println("InitMethodBean: setMessage = " + message);
    }

    public void init() {
        System.out.println("InitMethodBean: custom init-method (init), message=" + message);
    }

    public void destroy() {
        System.out.println("InitMethodBean: custom destroy-method (destroy)");
    }
}
