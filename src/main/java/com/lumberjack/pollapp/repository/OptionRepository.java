package com.lumberjack.pollapp.repository;

import com.lumberjack.pollapp.model.Option;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends CrudRepository<Option, Integer> {
}
