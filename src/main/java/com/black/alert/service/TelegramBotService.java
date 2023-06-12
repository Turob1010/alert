package com.black.alert.service;

import com.black.alert.context.properties.ApplicationProperties;
import com.black.alert.domain.MessageEntity;
import com.black.alert.domain.inner.BotStep;
import com.black.alert.domain.inner.BotText;
import com.black.alert.model.*;
import com.black.alert.repository.AlertRepository;
import com.black.alert.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.black.alert.domain.inner.BotStep.SELECT_LANGUAGE;
import static com.black.alert.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class TelegramBotService {

  private static final Logger LOGGER = LogManager.getLogger();

  private final MessageRepository repository;
  private final AlertRepository alertRepository;
  private final WebClient webClientTelegram;
  private final WebClient webClientProduct;
    private final WebClient webClientAuth;
  private final ObjectMapper objectMapper;
  private final String productUrl;
  private final String homeUrl;
  private final BotText botText;

  public TelegramBotService(
      final ApplicationProperties properties,
      final MessageRepository repository,
      final AlertRepository alertRepository,
      final ObjectMapper objectMapper,
      final WebClient.Builder webClientBuilder,
      final BotText botText) {
    this.repository = repository;
    this.alertRepository = alertRepository;
    this.objectMapper = objectMapper;
    this.botText = botText;
    this.productUrl = properties.product().productUrl();
    this.homeUrl = properties.home().homeUrl();
    this.webClientTelegram =
        webClientBuilder
            .baseUrl(properties.telegramBot().baseUrl() + properties.telegramBot().token())
            .build();
    this.webClientProduct = webClientBuilder.baseUrl(properties.product().baseUrl()).build();
      this.webClientAuth = webClientBuilder.baseUrl(properties.authServer().baseUrl()).build();

  }

  //  7220f3bd-8c13-4fee-91f3-f5e8182e45a6
  public Mono<TelegramUpdate> existChatId(final String chat_id, final int messageId) {

    return repository
        .findByChatId(chat_id)
        .switchIfEmpty(
            Mono.defer(
                () ->
                    this.sendFirstMessage(chat_id)
                        .map(chat -> objectMapper.convertValue(chat, MessageEntity.class))))
        .flatMap(
            chat -> {
              if (chat.getStep() == SELECT_LANGUAGE) {
                return enterPasswordAndNumber(chat_id, messageId);
              } else {
                return checkUserForAlert(chat_id);
              }
            });
  }

  private Mono<TelegramUpdate> sendFirstMessage(final String chat_id) {
    SendMessage message = new SendMessage();
    message.setChatId(chat_id);
    message.setText("Botga xush kelibsiz!!!");

    return sendMessage(message).flatMap(bot -> selectBotLanguage(chat_id));
  }

  public Mono<TelegramUpdate> selectBotLanguage(final String chat_id) {
      InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> firstRow = new ArrayList<>();
      List<List<InlineKeyboardButton>> secondRow = new ArrayList<>();
      List<List<InlineKeyboardButton>> thirdRows = new ArrayList<>();

    List<InlineKeyboardButton> uzButton = new ArrayList<>();
    firstRow.add(uzButton);
    List<InlineKeyboardButton> engButton = new ArrayList<>();
    secondRow.add(engButton);
    List<InlineKeyboardButton> rusButton = new ArrayList<>();
    thirdRows.add(rusButton);
    SendMessage message = new SendMessage();
    message.setChatId(chat_id);
    message.setText(
        """
            Muloqot uchun tilni tanlang.\s
            Choose a language for communication\s
            Выберите язык для общения""");
    InlineKeyboardButton selectUzbLanguage = new InlineKeyboardButton();
    InlineKeyboardButton selectEngLanguage = new InlineKeyboardButton();
    InlineKeyboardButton selectRusLanguage = new InlineKeyboardButton();

    selectUzbLanguage.setText(UZB);
    selectUzbLanguage.setCallbackData(UZB_LANGUAGE);
    uzButton.add(selectUzbLanguage);

    selectEngLanguage.setText(ENG);
    selectEngLanguage.setCallbackData(ENG_LANGUAGE);
    engButton.add(selectEngLanguage);

    selectRusLanguage.setText(RUS);
    selectRusLanguage.setCallbackData(RUS_LANGUAGE);
    rusButton.add(selectRusLanguage);
    inlineKeyboardMarkup.setKeyboard(firstRow);
    inlineKeyboardMarkup.setKeyboard(secondRow);
    inlineKeyboardMarkup.setKeyboard(thirdRows);
    message.setReplyMarkup(inlineKeyboardMarkup);
    return sendMessage(message);
  }

    public Mono<TelegramUpdate> setLanguage(final String chat_id, final String languageName, final
   int messageId) {
      EditMessageText message = new EditMessageText();
      return repository
          .findByChatId(chat_id)
          .flatMap(
              entity -> {
                  entity.setLanguage(languageName);
                message.setChatId(chat_id);
                message.setMessageId(messageId);
                message.setText(botText.selectLanguageText(languageName));
                return sendEditMessage(message).then(repository.save(entity));
              })
          .switchIfEmpty(
              Mono.defer(
                  () -> {
                    MessageEntity entity = new MessageEntity();
                    entity.setId(UUID.randomUUID());
                    entity.setLanguage(languageName);
                    entity.setChatId(chat_id);
                    entity.setStep(SELECT_LANGUAGE);
                    return repository
                        .save(entity)
                        .then(enterPasswordAndNumber(chat_id, messageId))
                        .map(bot -> objectMapper.convertValue(bot, MessageEntity.class));
                  }))
          .map(chat -> objectMapper.convertValue(chat, TelegramUpdate.class));
    }


  private Mono<TelegramUpdate> enterPasswordAndNumber(final String chat_id, final int messageId) {
    LOGGER.info("enterPasswordAndNumber method ishladi");
    EditMessageText editMessage = new EditMessageText();
    return repository
        .findByChatId(chat_id)
        .flatMap(
            chat -> {
              editMessage.setChatId(chat_id);
              editMessage.setMessageId(messageId);
              editMessage.setText(botText.phoneNumberAndPassword(chat.getLanguage()));
              return sendEditMessage(editMessage);
            });
  }

  public Mono<TelegramUpdate> checkUser(final String chatId, final String text) {
    String[] textPart = text.split("/");
    String phoneNumber = textPart[0];
    String password = textPart[1];
    SendMessage message = new SendMessage();
    return repository
        .findByChatId(chatId)
        .flatMap(chat -> Mono.just(chat.getLanguage()).zipWith(userExists(phoneNumber)))
        .flatMap(
            tuples -> {
              String language = tuples.getT1();
              boolean userExists = tuples.getT2();
              if (!userExists) {
                List<InlineKeyboardButton> buttons = new ArrayList<>();
                InlineKeyboardButton singUpButton = new InlineKeyboardButton();

                singUpButton.setText(botText.buttonCheckIn(language));
                singUpButton.setUrl(homeUrl);
                message.setChatId(chatId);
                message.setText(
                    "Номер телефона или пароль введен неверно. \n"
                        + "Пожалуйста, проверьте и введите заново."
                        + "Eсли вы не зарегистрированы, нажмите кнопку ниже"
                        + "и зарегистрируйтесь с сайта. \uD83D\uDC47");
                buttons.add(singUpButton);
                message.setReplyMarkup(createButton(buttons));
                return sendMessage(message);
              }
              return authUser(new BasicLoginRequest(phoneNumber, password))
                  .flatMap(
                      basicLoginResponse ->
                          saveUser(
                              Objects.requireNonNull(basicLoginResponse.getBody()).userId(),
                              chatId));
            })
        .map(chat -> objectMapper.convertValue(chat, TelegramUpdate.class));
  }

  private Mono<Void> saveUser(UUID userId, String chatId) {

    return repository
        .findByChatId(chatId)
        .flatMap(
            messageEntity -> {
              messageEntity.setUserId(userId);
              messageEntity.setStep(BotStep.SING_UP);
              LOGGER.info("User ID {} and chat ID {} save successfully", userId, chatId);

              return repository
                  .save(messageEntity)
                  .map(chat -> objectMapper.convertValue(chat, MessageChat.class))
                  .flatMap(user -> checkUserForAlert(chatId));
            })
        .then();
  }

  private Mono<TelegramUpdate> checkUserForAlert(final String chat_id) {
    SendMessage message = new SendMessage();
    return repository
        .findByChatId(chat_id)
        .flatMap(
            chat ->
                Mono.just(chat.getLanguage())
                    .zipWith(alertRepository.existsByUserId(chat.getUserId())))
        .flatMap(
            tuples -> {
              String language = tuples.getT1();
              boolean userExist = tuples.getT2();
              if (!userExist) {
                InlineKeyboardButton passSiteButton = new InlineKeyboardButton();
                List<InlineKeyboardButton> rowButtons = new ArrayList<>();
                passSiteButton.setText(HOWMUCH);
                passSiteButton.setUrl(homeUrl);
                message.setChatId(chat_id);
                message.setText(
                    "Вы зарегистрированы, нажмите кнопку ниже. \n"
                        + "Выберите лучшую цену для себя, просмотрев наши продукты, \n"
                        + "и мы сообщим вам, когда продукт будет по цене, которую вы ожидаете.\uD83D\uDE0A");
                rowButtons.add(passSiteButton);
                message.setReplyMarkup(createButton(rowButtons));
                return sendMessage(message);
              }
              message.setChatId(chat_id);
              message.setText(
                  "Вы успешно зарегистрировались. ✅ \n"
                      + "Пожалуйста, дождитесь изменения цены товара.");
              return sendMessage(message);
            });
  }

  Mono<TelegramUpdate> deleteAlertProduct(
      final UUID alertId, final String chat_id, final String language, final int messageId) {
    EditMessageText editMessageText = new EditMessageText();
    return alertRepository
        .findById(alertId)
        .flatMap(alert -> Mono.just(alert).zipWith(fetchProduct(alert.getProductId())))
        .flatMap(
            tuples -> {
              ProductNotification product = tuples.getT2();
              editMessageText.setChatId(chat_id);
              editMessageText.setMessageId(messageId);
              editMessageText.setText("Product  " + product.name() + "  deleted from Alert");
              return sendEditMessage(editMessageText);
            })
        .flatMap(alert -> alertRepository.deleteById(alertId))
        .map(result -> objectMapper.convertValue(result, TelegramUpdate.class));
  }

  private InlineKeyboardMarkup createButton(final List<InlineKeyboardButton> rowInlineButton) {
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    rowsInline.add(rowInlineButton);
    inlineKeyboardMarkup.setKeyboard(rowsInline);
    return inlineKeyboardMarkup;
  }

  private Mono<TelegramUpdate> sendMessage(final SendMessage message) {
    LOGGER.info("sendMessage method chatId {}, text {}", message.getChatId(), message.getText());
    return webClientTelegram
        .post()
        .uri("/sendMessage")
        .body(Mono.just(message), SendMessage.class)
        .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(TelegramUpdate.class);
  }

  private Mono<TelegramUpdate> sendEditMessage(final EditMessageText editMessage) {
    LOGGER.info(
        "sendMessage method chatId {}, text {}", editMessage.getChatId(), editMessage.getText());
    return webClientTelegram
        .post()
        .uri("/editMessageText")
        .body(Mono.just(editMessage), SendMessage.class)
        .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(TelegramUpdate.class);
  }

  public Mono<TelegramUpdate> sendPhoto(final SendPhoto photo) {
    LOGGER.info(
        "sendPhoto method chatId {}, photo {}",
        photo.getChatId(),
        photo.getPhoto().getAttachName());
    try {
      return webClientTelegram
          .post()
          .uri(
              "/sendPhoto?chat_id={chat_id}&photo={photo}&reply_markup={reply_markup}",
              photo.getChatId(),
              photo.getPhoto().getAttachName(),
              objectMapper.writeValueAsString(photo.getReplyMarkup()))
          .bodyValue(photo)
          .accept(APPLICATION_JSON)
          .retrieve()
          .bodyToMono(TelegramUpdate.class);
    } catch (JsonProcessingException e) {
      final String errorMsg =
          String.format(
              "When the check user, failed send photo to chat ID [%s]", photo.getChatId());
      LOGGER.error(errorMsg, e);
      throw new RuntimeException(errorMsg, e);
    }
  }


  private Mono<ResponseEntity<BasicLoginResponse>> authUser(BasicLoginRequest request) {

    Assert.notNull(request, "BasicLogin request cannot be null");
    return webClientAuth
        .post()
        .uri("/api/basic/login")
        .bodyValue(request)
        .accept(APPLICATION_JSON)
        .retrieve()
        .toEntity(BasicLoginResponse.class);
  }

    private Mono<Boolean> userExists(final String phoneNumber) {
        return webClientProduct
                .get()
                .uri("/exists/{phoneNumber}", phoneNumber)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

  private Mono<ProductNotification> fetchProduct(final UUID id) {
    return webClientProduct
        .get()
        .uri("/products/alert/{id}", id)
        .retrieve()
        .bodyToMono(ProductNotification.class);
  }

  private Mono<String> findChatId(final UUID userId) {
    return repository.findByUserId(userId).map(MessageEntity::getChatId);
  }
}
