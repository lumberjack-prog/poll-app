package com.lumberjack.pollapp.repository;

import com.lumberjack.pollapp.model.Poll;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends CrudRepository<Poll, Integer> {
}
