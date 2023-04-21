package com.lumberjack.pollapp.service;

import com.lumberjack.pollapp.model.Option;
import com.lumberjack.pollapp.model.Poll;

import java.util.List;

public interface IRedisDao {

    List<Option> getAllOptions();
    Option getOptionByName(String name);

    void saveAllOptions(List<Option> options);

    Option saveOption(Option option);

    Poll savePoll(Poll poll);

    List<Poll> getAllPolls();
}
