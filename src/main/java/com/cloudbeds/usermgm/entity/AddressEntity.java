package com.cloudbeds.usermgm.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table("address")
public class AddressEntity {

    @Id
    private Integer addressId;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
