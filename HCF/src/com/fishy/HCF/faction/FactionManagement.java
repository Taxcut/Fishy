package com.fishy.hcf.faction;

public class FactionManagement extends RuntimeException{

    public FactionManagement(String name){
        super("No faction found: " + name);
    }
}
