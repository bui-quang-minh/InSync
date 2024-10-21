package com.in_sync.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Action
{
    public int index;
    public String actionType;
    public boolean isLog;
    public String logContent;
    public int duration;
    public String direction;
    public String on;
    public float x;
    public float y;
    public float xPercent;
    public float yPercent;
    public int degrees;
    public List<com.in_sync.models.Action> executeActions;
    public int times;
    public String open;
}
