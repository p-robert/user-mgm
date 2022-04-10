package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Integer> {
    @Query("SELECT DISTINCT users FROM users JOIN u.addresses ad WHERE ad.country = :country")
    Flux<UserEntity> findUsersByCountry(String country);
}
