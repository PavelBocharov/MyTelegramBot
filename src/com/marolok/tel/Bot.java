package marolok.tel;

import marolok.utils.TelegramUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
	public static Logger logger = Logger.getLogger( Bot.class );

	private String BOT_USERNAME;
	private String BOT_TOKEN;
	private String SAVE_DIR;

	private TelegramUtils telegramUtils;

	public Bot() {
		Properties prop = new Properties();
		try {
			prop.load( getClass().getClassLoader().getResourceAsStream( "application.properties" ) );
			BOT_USERNAME = prop.getProperty( "botUsername" );
			BOT_TOKEN = prop.getProperty( "botToken" );
			SAVE_DIR = prop.getProperty( "saveDir" );
			telegramUtils = new TelegramUtils(this);
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

	public String getSavePath() {
		return SAVE_DIR;
	}

	public String getHelp() {
		return "Привет, я бот. Меня зовут " + BOT_USERNAME + ".\n" +
				"Да, мой создатель дебил и в свое время угорал по WarCraft и ебучим эльфам.\n" +
				"Ты можешь пользоваться следующими функциями:\n" +
				"/foto@" + BOT_USERNAME + " - я скину тебе картинку,\n" +
				"/gif@" + BOT_USERNAME + " - я скину тебе гифку,\n" +
				"/video@" + BOT_USERNAME + " - я скину тебе видюшку,\n" +
				"/help@" + BOT_USERNAME + " - помощь.";
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			checkMessage( update.getMessage() );
		}
		catch (Exception e) {
			logger.error( e.getMessage() );
			telegramUtils.sendMsg(update.getMessage(), "Произошло что-то страшное 0_0");
		}
	}

	public String getCommand(String message) {
		int endDel = message.indexOf("@" + BOT_USERNAME);
		if (endDel != -1) {
			return message.substring(0, endDel);
		}
		return message;
	}

	private void checkMessage(Message message) throws TelegramApiException, IOException {
		Objects.nonNull( message );
		if ( message.hasText() ) {
			BotCommand command = BotCommand.getBotCommand(getCommand(message.getText()));
			switch (command) {
				case START:
				case HELP: {
					telegramUtils.sendMsg( message, getHelp() );
					break;
				}
				case FOTO:
				case PHOTO: {
					telegramUtils.sendFoto( message );
					break;
				}
				case GIF: {
					telegramUtils.sendDocument(message, "gif");
					break;
				}
				case VIDEO: {
					telegramUtils.sendDocument(message, "video");
					break;
				}
			}
			if (message.hasEntities()) {
				telegramUtils.downloadByUrl(message.getEntities(), message.getText());
				return;
			}
			if (message.hasText()) {
				telegramUtils.sendMsg(message, "Окей. Ну я не понял.");
				return;
			}
			return;
		}
		if ( message.hasPhoto() ) {
			telegramUtils.downloadFileByFileID( telegramUtils.getBigPhoto(message.getPhoto()).getFileId() );
			return;
		}
		if (message.hasDocument()) {
			telegramUtils.downloadFileByFileID( message.getDocument().getFileId() );
			return;
		}
		if (message.getVoice() != null) {
			telegramUtils.downloadFileByFileID( message.getVoice().getFileId() );
			return;
		}
		if (message.getVideo() != null) {
			telegramUtils.downloadFileByFileID( message.getVideo().getFileId() );
			return;
		}
		if (message.getVideoNote() != null) {
			telegramUtils.downloadFileByFileID( message.getVideoNote().getFileId() );
			return;
		}
	}
}
