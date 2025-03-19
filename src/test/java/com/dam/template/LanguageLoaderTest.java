package com.dam.template;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LanguageLoaderTest {

  @Test
  void correctFormat_success() {
    var actual = LanguageLoader.load("test-success");
    var translations = actual.translations();
    assertAll(
        () -> assertEquals("test-success", actual.locale()),
        () -> assertEquals(UTF_8, actual.charset()),
        () -> assertEquals("This is a value", translations.get("key").get(0)),
        () -> assertEquals("This is a\n      multiline value", translations.get("key2").get(0)),
        () -> assertEquals("The = is escaped", translations.get("key3").get(0))
    );
  }

  @Test
  void incorrectFormat_failure() {
    assertThrows(IllegalStateException.class, () -> LanguageLoader.load("test-failure"));
  }
}