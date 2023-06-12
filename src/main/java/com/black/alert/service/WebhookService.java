package com.black.alert.service;


import com.black.alert.model.TelegramUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.black.alert.util.Constants.ALERT;
import static com.black.alert.util.Constants.START;


@Service
public class WebhookService {
  private static final Logger LOGGER = LogManager.getLogger();
  private final TelegramBotService telegramService;

  public WebhookService(final TelegramBotService telegramService) {
    this.telegramService = telegramService;
  }

  public Mono<TelegramUpdate> telegramUpdate(final Update update) {
    LOGGER.info("0000 telegramUpdate yetib keldi");
    if (update.hasMessage()) {
      String text = update.getMessage().getText();
      String chat_id = update.getMessage().getChatId().toString();
      Integer messageId = update.getMessage().getMessageId();
      if ((START).equals(text)) {
        return telegramService.existChatId(chat_id, messageId);
      } else if (validatePhoneNumberAndPassword(text)) {
        return telegramService.checkUser(chat_id, text);
      } else if ((ALERT).equals(text)) {
        return telegramService.findUserAllAlertProducts(chat_id);
      } else if (("/language").equals(text)) {
        return telegramService.selectBotLanguage(chat_id);
      }
    } else {
      if (update.hasCallbackQuery()) {
        String callData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        if (callData.contains(":")) {
          String[] textPart = callData.split(":");
          String language = textPart[0];
          UUID alertId = UUID.fromString(textPart[1]);
          //          if ((DA + alertId).equals(callData)) {
          return telegramService.deleteAlertProduct(alertId, chatId, language, messageId);
          //          }
        } else {
          return telegramService.setLanguage(chatId, callData, messageId);
        }
      }
    }
    return Mono.empty();
  }


  private static boolean validatePhoneNumberAndPassword(final String phoneNumberAndPassword) {
    LOGGER.info("Phone number and Password check  out in method");
    final String UZ_PHONE_NUMBER_PATTERN = "^[0-9]\\d{11}/.*$";
    return Pattern.matches(UZ_PHONE_NUMBER_PATTERN, phoneNumberAndPassword);
  }
}
