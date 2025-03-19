package com.dam.template;

import static com.dam.util.RandomUtil.formallyDistributed;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public record Language(String locale, Charset charset, Map<String, List<String>> translations) {

  private static final Pattern PLACEHOLDER_RGX = Pattern.compile("\\$\\{\\S+}");

  private static String interpolate(String template, Placeholder[] params) {
    var matcher = PLACEHOLDER_RGX.matcher(template);

    var result = new StringBuilder();
    while (matcher.find()) {
      var group = matcher.group();
      var key = group.substring(2, group.length() - 1);
      matcher.appendReplacement(result, getValue(key, params));
    }
    matcher.appendTail(result);

    return result.isEmpty()
        ? template // No placeholders found
        : result.toString();
  }

  private static String getValue(String key, Placeholder[] params) {
    for (var param : params) {
      if (param.name().equals(key)) {
        return String.valueOf(param.value());
      }
    }

    throw new IllegalStateException("Placeholder not found for :: " + key);
  }

  public String get(String key, Placeholder... params) {
    var templates = translations.get(key);
    if (templates == null) {
      throw new IllegalStateException(key + " not added in language pack for " + locale);
    }
    var template = formallyDistributed(templates);
    return interpolate(template, params);
  }
}
