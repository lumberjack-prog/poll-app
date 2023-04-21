package com.lumberjack.pollapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PollDto implements Serializable {
    private String key;
    private String optionName;

    public PollDto(Option option) {
        this.key = option.getKey();
        this.optionName = option.getOptionName();
    }
}
