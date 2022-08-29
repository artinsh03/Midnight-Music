package com.noone.midnightmusic.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.noone.jlogger.JLogger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class FilesHelper {

	private JLogger jlogger;

	public FilesHelper() {
		jlogger = new JLogger();
	}

//	===Load MP3 files from a directory
	public List<File> getMusicFiles(String path) {
		List<File> data = new ArrayList<File>();
		File[] files = new File(path).listFiles();
		for (File f : files) {
			if (!f.isDirectory() && f.canRead() && f.getName().endsWith(".mp3")) {
				data.add(f);
			}else if(f.isDirectory()) {
				data.addAll(getMusicFiles(f.getAbsolutePath()));
			}
		}
		return data;
	}

//	===Cache Music cover image into a directory 
	public String cacheImage(Image image, String name) {
		try {
			File folder = new File(System.getProperty("user.home") + "/.midnightmusic");
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File output = new File(System.getProperty("user.home") + "/.midnightmusic/" + name + ".png");
			BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
			ImageIO.write(bi, "png", output);
			return output.getAbsolutePath();
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return "failed";
		}

	}

//	===Get music duration by second
	public double getMusicDuration(String path) {

		try {
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(new File(path));
			Map<?, ?> metadata = ((TAudioFileFormat) fileFormat).properties();
			if (fileFormat instanceof TAudioFileFormat) {
				String key = "duration";
				Long microseconds = (Long) metadata.get(key);
				int mili = (int) (microseconds / 1000);
				int sec = (mili / 1000) % 60;
				int min = (mili / 1000) / 60;
				return sec + (min * 60);
			} else {
				return 0;
			}
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return 0;
		}
	}
}
