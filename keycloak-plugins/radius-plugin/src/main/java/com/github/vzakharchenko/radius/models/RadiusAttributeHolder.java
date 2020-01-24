package com.github.vzakharchenko.radius.models;

import com.github.vzakharchenko.radius.radius.handlers.attributes.KeycloakAttributesType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RadiusAttributeHolder<T> {
    private final KeycloakAttributesType type;
    private final T object;
    private Map<String, Set<String>> attributes = new HashMap<>();

    public RadiusAttributeHolder(KeycloakAttributesType type, T object) {
        this.type = type;
        this.object = object;
    }

    public void addAttribute(String name, Set<String> value) {
        attributes.put(name, value);
    }

    public Map<String, Set<String>> getAttributes() {
        return attributes;
    }

    public KeycloakAttributesType getType() {
        return type;
    }

    public T getObject() {
        return object;
    }

    public void filter(Predicate<Map.Entry<String, Set<String>>> predicate) {
        this.attributes = attributes.entrySet()
                .stream().filter(predicate)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
