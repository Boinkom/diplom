package org.example.telegram;

import lombok.Getter;
import org.example.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.*;
import java.util.Set;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Getter
    private int pauseSlide;
    private String otvetNumber;
    private final Set<String> chatIds = new HashSet<>();


    private final Map<Long, Boolean> waitingForAnswer = new HashMap<>();
    private final Map<Long, String> correctAnswer = new HashMap<>();

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        chatIds.add(String.valueOf(chatId));

        if (waitingForAnswer.getOrDefault(chatId, false)) {
            handleAnswer(chatId, messageText);
            return;
        }

        if (messageText.startsWith("/start")) {
            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
        }

        if (messageText.startsWith("/que")) {
            setPauseSlide(chatId, messageText);
        }
    }

    private void setPauseSlide(long chatId, String messageText) {
        try {
            String[] parts = messageText.split("\\s+");
            if (parts.length == 3) {
                pauseSlide = Integer.parseInt(parts[1]);
                otvetNumber = parts[2];
                correctAnswer.put(chatId, otvetNumber);
                waitingForAnswer.put(chatId, false);

                sendMessage(chatId, "Теперь пауза будет после слайда " + pauseSlide);
            } else {
                sendMessage(chatId, "Неверный формат команды. Используйте: /que <номер_слайда> <номер_ответа>");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный номер слайда. Используйте: /que <номер_слайда> <номер_ответа>");
        }
    }

    public void sendAllMessages(String textToSend) {
        for (String chatIdStr : chatIds) {
            long chatId = Long.parseLong(chatIdStr);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatIdStr);
            sendMessage.setText(textToSend);
            try {
                execute(sendMessage);
                waitingForAnswer.put(chatId, true);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAnswer(long chatId, String messageText) {


        if (messageText.equals(otvetNumber)) {
            sendMessage(chatId, "Верно! Продолжаем презентацию.");
        } else {
            sendMessage(chatId, "Неверно! Продолжаем презентацию.");
        }


        waitingForAnswer.remove(chatId);
    }

    private void startCommandReceived(long chatId, String userName) {
        sendMessage(chatId, "You, " + userName + "xuesos");
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
