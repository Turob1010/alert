package com.black.alert.context.properties;

import org.springframework.boot.context.properties.ConstructorBinding;

public record BotProperties(String name, String token, String baseUrl, String homeUrl) {
  public BotProperties(
      final String name, final String token, final String baseUrl, final String homeUrl) {
    this.name = name;
    this.token = token;
    this.baseUrl = baseUrl;
    this.homeUrl = homeUrl;
  }
}
