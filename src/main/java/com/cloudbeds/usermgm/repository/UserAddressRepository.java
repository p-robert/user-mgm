package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.entity.UserAddressEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserAddressRepository extends ReactiveCrudRepository<UserAddressEntity, Integer> {
    Flux<UserAddressEntity> findAllByUserId(Integer userId);
}
