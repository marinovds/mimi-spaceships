package com.dam.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;

public enum JsonUtil {
  ;

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule());

  public static <T> String write(T value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot write value", e);
    }
  }

  public static <T> T read(String value, Class<T> clazz) {
    try {
      return MAPPER.readValue(value, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot read value", e);
    }
  }

  public static <T> T read(String value, TypeReference<T> reference) {
    try {
      return MAPPER.readValue(value, reference);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot read value", e);
    }
  }

  public static <T> List<T> readList(String value) {
    if (value == null) {
      return List.of();
    }

    return read(value, new TypeReference<>() {});
  }
}
