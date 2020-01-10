package ua.zaskarius.keycloak.plugins.radius.radius;

import org.tinyradius.attribute.AttributeType;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.Dictionary;

import java.lang.reflect.Field;

public final class RadiusLibraryUtils {
    private RadiusLibraryUtils() {
    }

    private static void replaceTypeType(AttributeType attributeType, int type) {
        try {
            Field typeCode = AttributeType.class.getDeclaredField("typeCode");
            try {
                typeCode.setAccessible(true);
                typeCode.set(attributeType, type);
            } finally {
                typeCode.setAccessible(false);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static RadiusAttribute get26Attribute(
            Dictionary dictionary,
            int vendor,
            String attraibuteName,
            int newType,
            String value) {
        AttributeType attributeType = new AttributeType(vendor,
                254, attraibuteName,
                "string");
        replaceTypeType(attributeType, newType);


        return attributeType
                .create(dictionary, value);
    }
}
