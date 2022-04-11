package com.cloudbeds.usermgm.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table("user_address")
public class UserAddressEntity {
    @Id
    @Column(value = "id")
    private Integer id;

    @Column(value = "user_id")
    private Integer userId;
    @Column(value = "address_id")
    private Integer addressId;
}
