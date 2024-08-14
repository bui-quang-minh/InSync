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
    public int parent;
    public String action;
    public String actionType;
    public String on;
    public boolean logResult;
    public int duration;
    public int tries;
    public String conditionType;
    public String condition;
    public List<Action> isTrue;
    public List<Action> isFalse;
}
