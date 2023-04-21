package com.lumberjack.pollapp.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class AttributeBindingModel {
    private boolean userVoted;
    private List<Poll> allPolls;
}
