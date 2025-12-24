package com.aws.authportal.repository;

import com.aws.authportal.entity.DayOff;
import com.aws.authportal.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayOffRepository extends CrudRepository<DayOff, String> {
}
