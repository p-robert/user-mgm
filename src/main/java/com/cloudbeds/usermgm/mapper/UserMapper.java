package com.cloudbeds.usermgm.mapper;

import com.cloudbeds.usermgm.entity.UserEntity;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    UserEntity toEntity(final CreateUserRequest createUserRequest);

    @Mappings({
            @Mapping(target = "addresses", ignore = true),
            @Mapping(target = "userId", source = "id")
    })
    UserResponse toResponse(UserEntity userEntity);

}
