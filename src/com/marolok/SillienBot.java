package com.marolok;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class SillienBot extends TelegramLongPollingBot {
	public static Logger logger = Logger.getLogger( SillienBot.class );

	public String BOT_USERNAME;
	public String BOT_TOKEN;

	public SillienBot() {
		Properties prop = new Properties();
		try {
			prop.load( getClass().getClassLoader().getResourceAsStream( "application.properties" ) );
			BOT_USERNAME = prop.getProperty( "botUsername" );
			BOT_TOKEN = prop.getProperty( "botToken" );
		}
		catch (Exception ex) {
			logger.error( ex.getMessage() );
		}
	}

	@Override
	public String getBotUsername() {
		return BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

	@Override
	public void onUpdateReceived(Update update) {
		checkMessage( update.getMessage() );
	}

	private void checkMessage(Message message) {
		Objects.nonNull( message );
		if ( message.hasText() ) {
			if ( message.getText().equals( "/help@" + BOT_USERNAME ) ) {
				sendMsg( message, "Привет, я бот. Меня зовут Sillien.\n" +
						"Да, мой создатель дебил и в свое время угорал по WarCraft и ебучим эльфам.\n" +
						"Ты можешь пользоваться следующими функциями:\n" +
						"/foto@Sillien - я скину тебе красивую картинку,\n\n\n" +
						"/help@Sillien - помощь." );
			}
			if ( message.getText().equals( "/foto" + "@" + BOT_USERNAME ) ) {
				sendFoto( message );
			}
		}
		if ( message.hasPhoto() ) {
			List<PhotoSize> photos = message.getPhoto();
			for ( PhotoSize photo : photos ) {

			}
		}

	}

	private void sendMsg(Message message, String text) {
		logger.debug( String.format( "sendMessage - text: %s, message: %s", text, message ) );
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown( true );
		sendMessage.setChatId( message.getChatId().toString() );
		sendMessage.setReplyToMessageId( message.getMessageId() );

		sendMessage.setText( text );
		try {
			sendMessage( sendMessage );
		}
		catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void sendFoto(Message message) {
		SendPhoto photo = new SendPhoto();
		photo.setPhoto( "https://disgustingmen.com/wp-content/uploads/2018/03/5-1.jpg" );
		photo.setChatId( message.getChatId().toString() );
		photo.setReplyToMessageId( message.getMessageId() );

		try {
			sendPhoto( photo );
		}
		catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
