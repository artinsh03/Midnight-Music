package com.noone.midnightmusic.player;

import java.io.File;

import com.noone.midnightmusic.controller.HomeController;
import com.noone.midnightmusic.model.Music;
import com.noone.midnightmusic.util.FilesHelper;

import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {

	private Media media;
	private MediaPlayer player;
	private File file;
	private boolean isPlaying = false;
	
	
	
//	===Initialize MusicPlayer 
	public void setMusic(Music music) {
		 file = new File(music.getPath());
		 media = new Media(file.toURI().toString());
		 player = new MediaPlayer(media);

	}
//	===Play music and add a listener for seeking HomeController Slider by progressing music
	public void play() {
		player.play();
		player.setAudioSpectrumListener(new AudioSpectrumListener() {
			@Override
			public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
				HomeController.seek((int)timestamp);
				HomeController.changeCurrentPosition(timestamp);
			}
		});
		player.setOnEndOfMedia(new Runnable() {	
			@Override
			public void run() {
				HomeController.musicEnd();
				
			}
		});
		isPlaying = true;
	}
//	===Clear Player
	public void clear() {
		if(player != null) {
			player.stop();
			player = null;
		}
	}
//	===Seek music by percent
	public void seek(double percent) {
		if(player != null) {
//			===Get total time and calculate seek time by percent
			double duration = (int) new FilesHelper().getMusicDuration(file.getAbsolutePath());
			double time = (duration / 100) * percent;
			Duration seek = new Duration(time * 1000);
			player.seek(seek);
		}
	}
	
//	===Get music play status
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void resume() {
		if(player != null) {
			player.play();
			isPlaying = true;
		}
	}
	
	public void pause() {
		if(player != null) {
			player.pause();
			isPlaying = false;
		}
		
	}
	
	
	
	
}
