package com.black.alert.model;


import com.black.alert.domain.inner.BotStep;
import org.apache.commons.lang3.builder.Builder;

import java.util.UUID;

public record MessageChat(String chatId, UUID userId, BotStep step) {
  public MessageChat {}
}
