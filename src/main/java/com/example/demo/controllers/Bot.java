package com.example.demo.controllers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//clase hecha por Alvaro

public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.equals("/profesores")) {
              sendText(chatId, "Faltan x profesores");
            }
            
        }
    }

    @Override
    public String getBotUsername() {
        return "@Mi_bot_de_Telegram_2_bot";
    }

    @Override
    public String getBotToken() {
        return "6168074739:AAHW-o0A_yEQNGNAwENjmJrcEeWF28qe8LM";
    }

	public void sendText(long who, String what) {
	   SendMessage sm = SendMessage.builder()
	                    .chatId(Long.toString(who)) //Who are we sending a message to
	                    .text(what).build();    //Message content
	   try {
	        execute(sm);                        //Actually sending the message
	   } catch (TelegramApiException e) {
	        throw new RuntimeException(e);      //Any error will be printed here
	   }
	}
}
