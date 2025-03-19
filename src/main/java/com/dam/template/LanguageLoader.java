package com.dam.template;

import static com.dam.util.Util.readResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.dam.util.Visualizer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LanguageLoader {

  public static Language load(String locale) {
    // Loads and parses language translation packs (.ltp)
    var fileName = "languages/" + locale + ".ltp";
    var translations = translations(fileName);
    var language = new Language(locale, charset(translations), translations);
    Visualizer.language = language;
    return language;
  }

  private static Charset charset(Map<String, List<String>> translations) {
    var encodings = translations.getOrDefault("encodings", List.of());
    if (encodings.size() > 1) {
      throw new IllegalStateException("Improper formatting - multiple encoding specifies");
    }

    return encodings.stream()
        .findFirst()
        .filter(x -> !UTF_8.displayName().equalsIgnoreCase(x))
        .map(Charset::forName)
        .orElse(UTF_8);
  }

  private static Map<String, List<String>> translations(String fileName) {
    var tokens = tokenize(readResource(fileName));
    var result = new HashMap<String, List<String>>();
    Entry entry = null;
    for (var token : tokens) {
      if (token instanceof Definition d) {
        if (entry != null) {
          // If there are already present entry - flush
          var value = entry.value();
          result.compute(entry.key(), (k, v) -> tail(v, value));
        }
        entry = Entry.from(d);
      }

      // There cannot be a builder, without a definition.
      if (token instanceof Value v) {
        if (entry == null) {
          throw new IllegalStateException("Improperly formatted file.");
        }
        entry.appendValue(v);
      }

    }

    if (entry != null) {
      // get the last one
      var value = entry.value();
      result.compute(entry.key(), (k, v) -> tail(v, value));
    }

    return result;
  }

  private static <T> List<T> tail(List<T> existing, T val) {
    if (existing == null) {
      return List.of(val);
    }

    return Stream.concat(existing.stream(), Stream.of(val))
        .toList();
  }

  private static List<? extends Token> tokenize(List<String> lines) {
    return lines.stream()
        .map(x -> {
          if (x.trim().startsWith("--")) {
            return new Comment();
          }
          if (x.isBlank()) {
            return new Blank();
          }
          if (isDefinition(x)) {
            var pair = x.split("=", 2);
            return new Definition(pair[0].trim(), sanitize(pair[1]).trim());
          }

          return new Value(sanitize(x));
        })
        .filter(x -> !(x instanceof Comment || x instanceof Blank))
        .toList();
  }

  private static boolean isDefinition(String line) {
    var index = line.indexOf('=');

    return index != -1 && line.charAt(index - 1) != '\\'; // un-escaped '='
  }

  private static String sanitize(String value) {
    var index = value.indexOf("--");
    if (index == -1) {
      return value.replace("\\=", "=");
    }

    return value.replace("\\=", "=")
        .substring(0, index);
  }

  private sealed interface Token {

  }

  private record Comment() implements Token {

  }

  private record Blank() implements Token {

  }

  private record Definition(String name, String value) implements Token {

  }

  private record Value(String value) implements Token {

  }

  private record Entry(String key, StringBuilder builder) {

    static Entry from(Definition def) {

      return new Entry(def.name(), new StringBuilder(def.value()));
    }

    public Entry appendValue(Value value) {

      this.builder
          .append('\n')
          .append(value.value());
      return this;
    }

    public String value() {
      return builder.toString();
    }
  }

}
