package com.fishy.hcf.timer.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor
public class CustomTimer {

    @Getter public static List<CustomTimer> customTimers = new ArrayList<>();

    private String name;
    private String display;
    private long currentSecond;
    private String command;
}
