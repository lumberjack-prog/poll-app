package com.lumberjack.pollapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumberjack.pollapp.model.Option;
import com.lumberjack.pollapp.repository.OptionRepository;
import com.lumberjack.pollapp.service.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.*;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "com.lumberjack.pollapp.*")
public class PollAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(PollAppApplication.class, args);
    }


    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private RedisClient redisClient;

    @Bean
    CommandLineRunner runner(RedisService redisService, ObjectMapper objectMapper) {
        return args -> {

            List<Option> allOptions = redisService.getAllOptions();

            if (allOptions.size() == 0) {
                try(StatefulRedisConnection<String, String> connection = redisClient.connect();) {
                    RedisCommands<String, String> syncCommands = connection.sync();

                    List<Option> optionsList = List.of(
                            new Option("Java"),
                            new Option("JavaScript"),
                            new Option("Python"),
                            new Option("PHP"),
                            new Option("C#"));

                    optionsList.forEach(option -> {
                        try {
                            syncCommands.zadd("options", option.getPercentage(),
                                    objectMapper.writeValueAsString(option));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    List<String> options = syncCommands.zrevrange("options", 0, -1);
                    options.forEach(System.out::println);
                }
            }



//            List<Option> optionsArrayList = new ArrayList<>(List.of(option1, option2, option3, option4, option5));
//            ObjectMapper objectMapper = new ObjectMapper();
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(optionsArrayList);
//            System.out.println(json);
//
//            Iterable<Option> all = optionRepository.findAll();
//            for (Iterator<Option> it = all.iterator(); it.hasNext(); ) {
//                System.out.println(it.next());
//            }



            //       optionRepository.saveAll(List.of(option1, option2, option3, option4, option5));

//            List<Option> optionArrayList = new ArrayList<>(List.of(option1, option2, option3, option4, option5));
//
//            Poll poll = new Poll(new PollDto(option1), "john@domain.com");
//            option1.setPolls(option1.getPolls() + 1);
//            option1.setPercentage(option1.getPolls());
//
//            Poll poll1 = new Poll(new PollDto(option2), "dani@domain.com");
//            option2.setPolls(option2.getPolls() + 1);
//
//            Poll poll3 = new Poll(new PollDto(option1), "jack@domain.com");
//            option1.setPolls(option1.getPolls() + 1);
//
//            Poll poll4 = new Poll(new PollDto(option3), "denny@domain.com");
//            option3.setPolls(option3.getPolls() + 1);
//
//            double percentage = 0;
//            double count = optionArrayList.stream().mapToDouble(Option::getPolls).sum();
//            optionArrayList.forEach(option -> {
//                option.setPercentage(option.getPolls() * 100 / count);
//                System.out.println(option.getPercentage());
//            });
//            System.out.println(count);





//            String posrt1 = objectMapper.writeValueAsString(poll);
//            String posrt2 = objectMapper.writeValueAsString(poll1);




//            java.util.Map<String, String> polls = new HashMap<>();
//            polls.put("poll1", posrt1);
//            polls.put("poll2", posrt2);
//            Map<String, String> map = objectMapper
//                    .readValue(testData, new TypeReference<>() {
//                    });



      //      System.out.println(options1.get(0));


//            syncCommands.hset("polls", polls);
//            List<String> options1 = syncCommands.hvals("options");
//
//
//            String hget = syncCommands.hget("options", "option5");
//
//            Option getData = objectMapper.readValue(hget, new TypeReference<>() {
//            });

//            System.out.println(getData.getOptionName());

        };
    }
}
