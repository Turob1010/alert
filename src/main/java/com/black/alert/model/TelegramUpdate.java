package com.black.alert.model;

import org.telegram.telegrambots.meta.api.objects.Message;

public record TelegramUpdate(boolean ok, Message message) {}
