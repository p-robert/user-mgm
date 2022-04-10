package com.cloudbeds.usermgm.mapper;

import com.cloudbeds.usermgm.entity.AddressEntity;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AddressMapper {
    AddressResponse toResponse(AddressEntity addressEntity);

    @Mappings({
            @Mapping(target = "addressId", ignore = true)
    })
    AddressEntity toEntity(CreateAddressRequest createAddressRequest);

}
