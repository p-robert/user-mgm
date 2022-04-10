package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.entity.AddressEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface AddressRepository extends ReactiveCrudRepository<AddressEntity, Integer> {
    Flux<AddressEntity> findAllByAddressIdIn(List<Integer> addressesIds);

    Mono<AddressEntity> findAddressByZip(String zip);
}
