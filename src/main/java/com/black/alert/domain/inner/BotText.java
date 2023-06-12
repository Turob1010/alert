package com.black.alert.domain.inner;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.black.alert.util.Constants.*;


@Service
public class BotText {

  public String phoneNumberAndPassword(final String language) {

    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "telefon raqam va parolni kiriting \n" + "masalan: 998941112233/parol");
    map.put(
        ENG_LANGUAGE,
        "entering  your password and phone number \n" + "For example: 998941112233/password");
    map.put(
        RUS_LANGUAGE,
        "Bведите свой номер телефона и пароль. \n" + " Например: 998941112233/пароль");
    return map.get(language);
  }

  public String captionProductName(final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "Mahsulot nomi: ");
    map.put(ENG_LANGUAGE, "\n Product of name: ");
    map.put(RUS_LANGUAGE, "\n Продукт имени: ");
    return map.get(language);
  }

  public String captionProductPrice(final String language) {

    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "Mahsulot narxi: ");
    map.put(ENG_LANGUAGE, "\n Product of Price: ");
    map.put(RUS_LANGUAGE, "\n Продукт цены: ");
    return map.get(language);
  }

  public String captionAlertThreshold(final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "Kutilgan narx: ");
    map.put(ENG_LANGUAGE, "\n Threshold: ");
    map.put(RUS_LANGUAGE, "Ожидаемая цена :");
    return map.get(language);
  }

  public String buttonShowsProduct(final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "Mahsulotga o'ting");
    map.put(ENG_LANGUAGE, "Go product");
    map.put(RUS_LANGUAGE, "Перейти продукт");
    return map.get(language);
  }

  public String buttonCheckIn(final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "ro'yxatdan o'tish");
    map.put(ENG_LANGUAGE, "check up");
    map.put(RUS_LANGUAGE, "регистрироваться");
    return map.get(language);
  }

  public String buttonDeleteAlertMap(final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "Ogohlantirishni o'chirish");
    map.put(ENG_LANGUAGE, "Deleting alert");
    map.put(RUS_LANGUAGE, "Удалить оповещение");
    return map.get(language);
  }

  public String selectLanguageText (final String language) {
    Map<String, String> map = new HashMap<>();
    map.put(UZB_LANGUAGE, "\uD83C\uDDFA\uD83C\uDDFF O'zbek tili tanlandi");
    map.put(ENG_LANGUAGE, "\uD83C\uDDEC\uD83C\uDDE7 English is selected");
    map.put(RUS_LANGUAGE, "\uD83C\uDDF7\uD83C\uDDFA Выбран русский язык");
    return map.get(language);
  }
}
