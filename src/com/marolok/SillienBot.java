package com.marolok;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class SillienBot extends TelegramLongPollingBot {
	public static Logger logger = Logger.getLogger( SillienBot.class );

	private String BOT_USERNAME;
	private String BOT_TOKEN;
	private String SAVE_DIR;

	public SillienBot() {
		Properties prop = new Properties();
		try {
			prop.load( getClass().getClassLoader().getResourceAsStream( "application.properties" ) );
			BOT_USERNAME = prop.getProperty( "botUsername" );
			BOT_TOKEN = prop.getProperty( "botToken" );
			SAVE_DIR = prop.getProperty( "saveDir" );
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
		try {
			checkMessage( update.getMessage() );
		}
		catch (TelegramApiException e) {
			logger.error( e.getMessage() );
			e.printStackTrace();
		}
		catch (IOException e) {
			logger.error( e.getMessage() );
			e.printStackTrace();
		}
	}

	private void checkMessage(Message message) throws TelegramApiException, IOException {
		Objects.nonNull( message );
		if ( message.hasText() ) {
			if ( message.getText().equalsIgnoreCase("/start") || message.getText().equalsIgnoreCase( "/help@" + BOT_USERNAME ) ) {
				sendMsg( message, "Привет, я бот. Меня зовут Sillien.\n" +
						"Да, мой создатель дебил и в свое время угорал по WarCraft и ебучим эльфам.\n" +
						"Ты можешь пользоваться следующими функциями:\n" +
						"/foto@Sillien - я скину тебе красивую картинку,\n\n\n" +
						"/help@Sillien - помощь." );
				return;
			}
			if ( message.getText().equalsIgnoreCase( "/foto" + "@" + BOT_USERNAME ) ) {
				sendFoto( message );
				return;
			}
			sendMsg( message, "Я блондинка, я не поняла." );
			System.out.println(message.getText());
		}
		if ( message.hasPhoto() ) {
			downloadFileByFileID( getBigPhoto(message.getPhoto()).getFileId() );
		}
		if (message.hasDocument()) {
			downloadFileByFileID( message.getDocument().getFileId() );
		}
		if (message.getVoice() != null) {
			downloadFileByFileID( message.getVoice().getFileId() );
		}
		if (message.getVideo() != null) {
			downloadFileByFileID( message.getVideo().getFileId() );
		}
		if (message.getVideoNote() != null) {
			downloadFileByFileID( message.getVideoNote().getFileId() );
		}
	}

	private PhotoSize getBigPhoto(List<PhotoSize> photos) {
		PhotoSize result = photos.get( 0 );
		for ( int i = 1; i < photos.size(); i++ ) {
			PhotoSize photo = photos.get( i );
			if (result.getFileSize() < photo.getFileSize()) {
				result = photo;
			}
		}
		return result;
	}

	private void downloadFileByFileID(String fileId) throws TelegramApiException, IOException {
		GetFile getFile = new GetFile();
		getFile.setFileId( fileId );
		File file = getFile( getFile );

		String urlString = file.getFileUrl( BOT_TOKEN );
		String fileString = SAVE_DIR + file.getFilePath();

		System.out.println(String.format( "Copy \nfrom url: %s\nto file:%s", urlString, fileString ));

		URL url = new URL( urlString );
		FileUtils.copyURLToFile( url, new java.io.File( fileString ) );
	}

	private void sendMsg(Message message, String text) {
		System.out.println( String.format( "sendMessage - text: %s, message: %s", text, message ) );
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
		photo.setChatId( message.getChatId().toString() );
		photo.setReplyToMessageId( message.getMessageId() );
		photo.setNewPhoto( getRandomLocalPhoto() );
		try {
			sendPhoto( photo );
		}
		catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private java.io.File getRandomLocalPhoto() {
		List<java.io.File> photos = FileUtil.getFiles( SAVE_DIR + "/photos" );
		int rand = randInt(0, photos.size() - 1);
		return photos.get( rand );
	}

	private int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
