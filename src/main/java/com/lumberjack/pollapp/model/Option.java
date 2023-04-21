package com.lumberjack.pollapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash
public class Option implements Serializable, Comparable<Option> {

    private static int currentId = 0;

    @Id
    private String key;
    @Indexed @NonNull
    private String optionName;
    @Indexed
    private double percentage = 0;
    @Indexed
    private int polls = 0;

    public Option(String optionName) {
        this.key = getNextId();
        this.optionName = optionName;
    }

    private static String getNextId() {
        return "option:" +  currentId++;
    }

    @Override
    public int compareTo(Option o) {
        if (o != null && o.getClass() == this.getClass()) {
            return Double.compare(this.percentage, o.percentage);
        } else {
            throw new RuntimeException("Comparing objects of type of StatsModel failed!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;
        return optionName.equals(option.optionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionName);
    }
}
