package com.lumberjack.pollapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumberjack.pollapp.model.AttributeBindingModel;
import com.lumberjack.pollapp.model.Option;
import com.lumberjack.pollapp.model.Poll;
import com.lumberjack.pollapp.model.PollDto;
import com.lumberjack.pollapp.util.Util;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RedisService implements IRedisDao {


    @Autowired
    RedisClient redisClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Option> getAllOptions() {
        log.info("Getting all options");

        List<Option> allOptions = new ArrayList<>();

        try (StatefulRedisConnection<String, String> connection = redisClient.connect();) {
            RedisCommands<String, String> syncCommands = connection.sync();
            List<String> optionsAsString = syncCommands.zrevrange("options", 0, -1);

            optionsAsString.forEach(optionAsString -> {
                try {
                    Option option = objectMapper.readValue(optionAsString, new TypeReference<>() {
                    });
                    allOptions.add(option);
                } catch (JsonProcessingException e) {
                    log.error("Failed to get all options: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            log.info("Getting all options! Total number: {}, Options: {}, ", allOptions.size(), allOptions);
        }
        return allOptions;
    }

    @Override
    public Option getOptionByName(String name) {
        log.info("Getting options by name: {}", name);

        List<Option> allOptions = getAllOptions();
        Optional<Option> first = allOptions.stream()
                .filter(option1 -> option1.getOptionName().equals(name))
                .findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            log.error("Option with name {} was not found.", name);
            throw new RuntimeException(String.format("Option with name: \"%s\" was not found. ", name));
        }
    }

    @Override
    public void saveAllOptions(List<Option> optionsList) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect();) {
            RedisCommands<String, String> syncCommands = connection.sync();

            optionsList.forEach(option -> {
                try {
                    syncCommands.zadd("options", option.getPercentage(),
                            objectMapper.writeValueAsString(option));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public Option saveOption(Option option) {
        log.info("Saving option: {}", option);
        List<Option> allOptions = getAllOptions();

        Optional<Option> option1 = allOptions.stream().filter(option2 -> option2.equals(option)).findFirst();

        if (option1.isPresent()) {
            Option optionFound = option1.get();

        } else {
            try {
                String optionAsString = objectMapper.writeValueAsString(option);
                // TODO: finish this block
            } catch (JsonProcessingException ex) {
                log.error("Saving option: {} failed. {}", option, ex.getMessage());
                ex.printStackTrace();
            }
        }
        return option;
    }

    @Override
    public Poll savePoll(Poll poll) {
        log.info("Saving poll: {}", poll);

        List<Poll> allPolls = getAllPolls();

        boolean exist = allPolls.stream().anyMatch(poll1 -> poll1.equals(poll));

        if (!exist) {
            try (StatefulRedisConnection<String, String> connection = redisClient.connect();) {
                RedisCommands<String, String> syncCommands = connection.sync();

                syncCommands.lpush("polls", objectMapper.writeValueAsString(poll));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.error("The poll: {} already exists", poll);
            throw new RuntimeException(String.format("The poll: %s already exists", poll));
        }

        return poll;
    }

    @Override
    public List<Poll> getAllPolls() {
        log.info("Getting all polls");

        List<Poll> allPolls = new ArrayList<>();
        try (StatefulRedisConnection<String, String> connection = redisClient.connect();) {
            RedisCommands<String, String> syncCommands = connection.sync();
            List<String> pollsAsString = syncCommands.lrange("polls", 0, -1);

            pollsAsString.forEach(poll -> {
                try {
                    Poll tmpPoll = objectMapper.readValue(poll, new TypeReference<>() {
                    });
                    allPolls.add(tmpPoll);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return allPolls;
    }

    public AttributeBindingModel checkIfVoted(String userEmail) {
        List<Poll> allPolls = getAllPolls();
        AttributeBindingModel attributeBindingModel = new AttributeBindingModel();
        boolean result = allPolls.stream().anyMatch(poll -> poll.getUserEmail().equals(userEmail));
        attributeBindingModel.setAllPolls(allPolls);
        attributeBindingModel.setUserVoted(result);
        return attributeBindingModel;
    }

    public void calculatePercentage(String optionSelected, String userEmail) {
        List<Option> allOptions = getAllOptions();
        PollDto pollDto = new PollDto();
        Poll poll = new Poll();
        poll.setInitProps();

        try (StatefulRedisConnection<String, String> connection = redisClient.connect();) {
            RedisCommands<String, String> syncCommands = connection.sync();
            allOptions.forEach(option -> {
                try {
                    syncCommands.zrem("options", objectMapper.writeValueAsString(option));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        allOptions.stream().filter(option -> option.getOptionName().equals(optionSelected))
                .findFirst().ifPresent(option -> {
                    option.setPolls(option.getPolls() + 1);
                    pollDto.setOptionName(option.getOptionName());
                    pollDto.setKey(option.getKey());
                    poll.setUserEmail(userEmail);
                    poll.setOptionSelected(pollDto);
                });


        if (allOptions.size() > 0) {
            int total = allOptions.stream().mapToInt(Option::getPolls).sum();
            allOptions.forEach(option -> option.setPercentage(Util.convertToDecimalWithTwoPlaces(option.getPolls() * 100 / (float) total)));
        }
        saveAllOptions(allOptions);
        savePoll(poll);
    }
}
