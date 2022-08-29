package com.noone.midnightmusic.model;

import java.io.File;
import java.sql.ResultSet;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import com.noone.jlogger.JLogger;
import com.noone.midnightmusic.util.FilesHelper;

public class Music {

	private int id;
	private String title;
	private String author;
	private String album;
	private String path;
	private String duration;
	private int folder;
	
	

	public int getFolder() {
		return folder;
	}

	public void setFolder(int folder) {
		this.folder = folder;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public Music fileToMusic(File file , int folderId) {
		JLogger jlogger = new JLogger();
		try {
			Music music = new Music();
			FilesHelper fh = new FilesHelper();
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
//		===Get music metadata like title , author , album and etc.
			Map<?, ?> metadata = ((TAudioFileFormat) fileFormat).properties();

//		===Retrieve music metadata
			String metaTitle = (metadata != null && metadata.get("title") != null) ? metadata.get("title").toString() : file.getName().replaceAll(".mp3", "");
			String metaAuthor = (metadata != null && metadata.get("author") != null) ? metadata.get("author").toString() : "Unknown";
			String metaAlbum = (metadata != null &&  metadata.get("album") != null) ? metadata.get("album").toString() : "Unknown";
//		===Set music metadata
			music.setTitle((!metaTitle.equals("")) ? metaTitle : file.getName().replaceAll(".mp3", ""));
			music.setAuthor(metaAuthor);
			music.setAlbum(metaAlbum);
			music.setPath(file.getAbsolutePath());


//		 ===Calculate duration
			double durSec = fh.getMusicDuration(file.getAbsolutePath());
			double min = durSec / 60;
			double sec = durSec % 60;
//		===Set duration
			String second = (sec < 10) ? "0" +(int) sec : String.valueOf((int)sec);
			String minute = (min < 10) ? "0" +(int) min : String.valueOf((int)min);
			music.setDuration(minute + ":" + second);

//			===Set folder
			music.setFolder(folderId);
			return music;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		}
	}

	public Music resultSetToMusic(ResultSet result) {
		try {
			Music music = new Music();
			music.setId(result.getInt("id"));
			music.setTitle(result.getString("title"));
			music.setAuthor(result.getString("author"));
			music.setPath(result.getString("path"));
			music.setDuration(result.getString("duration"));
			music.setFolder(result.getInt("folder"));
			return music;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
