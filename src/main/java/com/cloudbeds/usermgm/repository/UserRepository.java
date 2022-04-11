package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Integer> {
    @Query("SELECT u.* \n" +
            "FROM users u \n" +
            "         JOIN user_address ua on u.id = ua.user_id\n " +
            "         join address a on a.address_id = ua.address_id \n" +
            "WHERE a.country = :country")
    Flux<UserEntity> findUsersByCountry(String country);

    Mono<UserEntity> findUserByEmail(String email);
}
