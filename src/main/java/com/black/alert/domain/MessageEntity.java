package com.black.alert.domain;

import com.black.alert.domain.inner.BotStep;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import static com.black.alert.util.Constants.COLLECTION_BOT;


@Document(collection = COLLECTION_BOT)
public class MessageEntity {

  private UUID userId;

  private String chatId;
  @Id private UUID id;

  private String phoneNumber;

  private String password;

  private BotStep step;

  private String language;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public BotStep getStep() {
    return step;
  }

  public void setStep(BotStep step) {
    this.step = step;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public String toString() {
    return "MessageEntity{"
        + "userId="
        + userId
        + ", chatId='"
        + chatId
        + '\''
        + ", id="
        + id
        + ", phoneNumber='"
        + phoneNumber
        + '\''
        + ", password='"
        + password
        + '\''
        + ", step="
        + step
        + '\''
        + ", language='"
        + language
        + '}';
  }
}
