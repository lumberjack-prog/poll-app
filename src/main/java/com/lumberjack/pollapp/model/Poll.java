package com.lumberjack.pollapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@ToString
@Setter
@Getter
@RedisHash
@NoArgsConstructor
public class Poll implements Serializable, Comparable<Poll> {

    private static int currentId = 0;

    @Id
    private String key;
    @NonNull
    @Indexed
    private PollDto optionSelected;
    @NonNull
    @Indexed
    private String timeStamp;
    @NonNull
    @Indexed
    private String userEmail;

    public Poll(PollDto optionSelected, String userEmail) {
        this.key = getNextId();
        this.optionSelected = optionSelected;
        this.userEmail = userEmail;
        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    private static String getNextId() {
        return "poll:" + currentId++;
    }

    @Override
    public int compareTo(Poll o) {
        if(o != null && o.getClass() == this.getClass()) {
            return userEmail.compareTo(o.userEmail);
        }
        return -1;
    }

    public void setInitProps() {
        this.key = getNextId();
        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
