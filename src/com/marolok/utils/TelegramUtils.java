package marolok.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import marolok.tel.Bot;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TelegramUtils {

    public static Map<String, List<String>> mapFiles = ImmutableMap.of(
            "photos", ImmutableList.of(".jpg", ".jpeg", ".bmp", ".png"),
            "gif", ImmutableList.of(".gif")
    );

    private Bot bot;

    public TelegramUtils(Bot bot) {
        this.bot = bot;
    }

    public PhotoSize getBigPhoto(List<PhotoSize> photos) {
        PhotoSize result = photos.get(0);
        for (int i = 1; i < photos.size(); i++) {
            PhotoSize photo = photos.get(i);
            if (result.getFileSize() < photo.getFileSize()) {
                result = photo;
            }
        }
        return result;
    }

    public void downloadFileByFileID(String fileId) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        File file = bot.execute(getFile);

        String urlString = file.getFileUrl( bot.getBotToken() );
        String fileString = bot.getSavePath() + "/" + file.getFilePath();

        System.out.println(String.format( "Copy \nfrom url: %s\nto file:%s", urlString, fileString ));

        URL url = new URL( urlString );
        FileUtils.copyURLToFile( url, new java.io.File( fileString ) );
    }

    public void sendFoto(Message message) throws TelegramApiException {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(message.getChatId().toString());
        photo.setReplyToMessageId(message.getMessageId());
        photo.setPhoto(getRandomLocalFile("photos"));
        bot.execute(photo);
    }

    public void sendDocument(Message message, String dir) throws TelegramApiException {
        SendDocument document = new SendDocument();
        document.setChatId(message.getChatId());
        document.setReplyToMessageId(message.getMessageId());
        document.setDocument(getRandomLocalFile(dir));
        bot.execute(document);
    }

    private java.io.File getRandomLocalFile(String dir) {
        List<java.io.File> files = FileUtil.getFiles(bot.getSavePath() + "/" + dir);
        int rand = randInt(0, files.size() - 1);
        return files.get(rand);
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private String getFileNameByUrl(String url) {
        String[] strs = url.split("/");
        return strs.length > 0 ? strs[strs.length - 1] : "null";
    }

    private String getPathByFileName(String fileName) {
        if (fileName == null) throw new NullPointerException();
        String fileNameLowCase = fileName.toLowerCase();
        Set<String> keys = mapFiles.keySet();
        for (String key : keys) {
            for (String sufix : mapFiles.get(key)) {
                if (fileNameLowCase.endsWith(sufix)) return key;
            }
        }
        return null;
    }

    public void downloadByUrl(List<MessageEntity> entities, String url) throws IOException {
        for (MessageEntity entity : entities) {
            if ("url".equals(entity.getType())) {
                String fileName = getFileNameByUrl(url);
                String pathFile = getPathByFileName(fileName);
                java.io.File file = new java.io.File(bot.getSavePath() + (pathFile == null ? "" : pathFile), fileName);
                FileUtils.copyURLToFile(new URL(url), file);
            }
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMes = new SendMessage();
        sendMes.enableMarkdown(true);
        sendMes.setChatId(message.getChatId().toString());
        sendMes.setReplyToMessageId(message.getMessageId());
        sendMes.setText(text);
        try {
            bot.execute(sendMes);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
