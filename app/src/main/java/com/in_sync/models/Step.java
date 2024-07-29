package com.in_sync.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Step {
    private String actionType;
    private String on;
    private int duration;
    private int tries;
    private String content;
}
