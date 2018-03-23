package marolok;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Утилиты для работы с файлами и папками
 * @author bocharov_pv
 */
public class FileUtil {
	private static Logger log = Logger.getLogger( FileUtil.class );;

	/**
	 * Возвращает файл/папку
	 * @param fileName имя файла/папки
	 * @return
	 */
	public static File getFile(String fileName){
		File file = null;
		try{
			file = new File(fileName);
			if (!file.exists()) {
				log.error("File ["+fileName+"] not found.");
				return file;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return file;
	}

	/**
	 * Возвращает содержимое файла
	 * @param fileName имя файла
	 * @return
	 */
	public static String getFileValue(String fileName){
		return getFileValue(getFile(fileName));
	}

	private static String getFileValue(File file){
		try {
			return Files.toString(file, Charsets.UTF_8);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Возвращает список файлов в папке(включая подпапки)
	 * @param pathName имя файла
	 * @return
	 */
	public static List<File> getFiles(String pathName){
		return getFiles(getFile(pathName));
	}

	private static List<File> getFiles(File path){
		List<File> files = new LinkedList<>();
		if (path.exists()){
			File[] fileArr = path.listFiles();
			for (File file : fileArr) {
				if (file.isDirectory()){
					files.addAll(getFiles(file));
				} else {
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * Записывает содержимое value в файл(если файл есть то не запишет).
	 * @param fileName имя файла
	 * @param value содержимое
	 */
	public static void setFileValue(String fileName, String value){
		File file = getFile(fileName);
		if (!file.exists()) {
			setFileValue(file, value);
		} else {
			log.error("File ["+fileName+"] exists. If you want to overwrite file use 'setFileValueStrong'.");
		}
	}

	/**
	 * Записывает содержимое value в файл(если файл есть то перезапишет).
	 * @param fileName имя файла
	 * @param value содержимое
	 */
	public static void setFileValueStrong(String fileName, String value){
		setFileValue(getFile(fileName), value);
	}

	private static void setFileValue(File file, String value){
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			Files.write(value, file, Charsets.UTF_8);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}