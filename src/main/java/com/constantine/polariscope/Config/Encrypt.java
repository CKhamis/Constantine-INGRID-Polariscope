package com.constantine.polariscope.Config;

import com.constantine.polariscope.Util.EncryptionUtil;
import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Encrypt implements AttributeConverter<String,String> {

    @Autowired
    EncryptionUtil encryptionUtil;

    @Override
    public String convertToDatabaseColumn(String s) {
        return encryptionUtil.encrypt(s);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return encryptionUtil.decrypt(s);
    }
}
