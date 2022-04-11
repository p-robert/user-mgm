package com.cloudbeds.usermgm.converters;

import com.cloudbeds.usermgm.entity.UserEntity;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordConverter implements Converter<UserEntity, OutboundRow> {

    private static final String PBKDF_2_WITH_HMAC_SHA_1 = "PBKDF2WithHmacSHA1";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;

    @SneakyThrows
    @Override
    public OutboundRow convert(@NonNull UserEntity user) {
        OutboundRow row = new OutboundRow();
        if(user.getId() != null) {
            row.put("id", Parameter.from(user.getId()));
        }
        row.put("first_name", Parameter.from(user.getFirstName()));
        row.put("last_name", Parameter.from(user.getLastName()));
        row.put("email", Parameter.from(user.getEmail()));
        row.put("password", Parameter.from(getHashedPassword(user)));
        return row;
    }

    private String getHashedPassword(UserEntity user) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var spec = new PBEKeySpec(user.getPassword().toCharArray(), getSalt(), ITERATION_COUNT, KEY_LENGTH);
        var factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_1);
        return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
    }

    private byte[] getSalt() {
        var random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
