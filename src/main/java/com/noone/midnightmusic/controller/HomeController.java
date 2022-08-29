package com.noone.midnightmusic.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import com.noone.jlogger.JLogger;
import com.noone.midnightmusic.App;
import com.noone.midnightmusic.database.Database;
import com.noone.midnightmusic.model.Music;
import com.noone.midnightmusic.player.MusicPlayer;
import com.noone.midnightmusic.util.FilesHelper;
import javafx.beans.binding.Bindings;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;

public class HomeController implements Initializable {

	@FXML
	private Slider playerProgress;

	@FXML
	private AnchorPane homePane;
	
	@FXML
	private ImageView prev , next , playPause , repeat , currentImage;
	
	@FXML
	private Label currentDuration , currentPosition , currentTitle , currentAuthor;
	
	@FXML
	private Pane menuHome , menuSearch;
	
	private static AnchorPane statHomePane;
	
	private static ImageView  statPrev , statNext , statPlayPause , statCurrentImage;
	
	private static Label statCurrentDuration , statCurrentPosition , statCurrentTitle , statCurrentAuthor;

	private static MusicPlayer musicPlayer;

	private static Slider progress;
	
	private static Music currentMusic;

	private static JLogger jlogger;
	
	private static List<Music> musicList;
	
	private Map<String, String> configs;
	
	private static Database db;
	
	private static boolean isRepeat = false;

	public HomeController() {
		musicPlayer = new MusicPlayer();
		jlogger = new JLogger();
		db = new Database();
//		===Initialize Configuration
		configs = db.getConfig();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

//		===Set slider color
		playerProgress.styleProperty().bind(Bindings.createStringBinding(() -> {
			double percentage = (playerProgress.getValue() - playerProgress.getMin())
					/ (playerProgress.getMax() - playerProgress.getMin()) * 100.0;
			return String.format(
					"-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
							+ "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);",
					percentage, percentage);
		}, playerProgress.valueProperty(), playerProgress.minProperty(), playerProgress.maxProperty()));
		progress = playerProgress;

		
//		===Initialize static element's
		statPrev = prev;
		statNext = next;
		statPlayPause = playPause;
		statCurrentDuration = currentDuration;
		statCurrentPosition = currentPosition;
		statCurrentTitle = currentTitle;
		statCurrentAuthor = currentAuthor;
		statCurrentImage = currentImage;
		statHomePane = homePane;
		
//		===Load home_home.fxml into homePane
			loadFXMLSection("home_home");
			
		
		

//		===Set repeat status
		isRepeat =Boolean.parseBoolean(configs.get("repeat"));
		Image img;
		if(isRepeat) {
			 img = new Image(getResourceImage("repeat_one").toURI().toString());
		}else {
			 img = new Image(getResourceImage("repeat").toURI().toString());
		}
		repeat.setImage(img);

	}

	@FXML
	private void exit() {
		App.exit();
	}

	@FXML
	private void minimize() {
		App.minimize();
	}
	
	@FXML
	public void musicPlayPause() {
		Image pauseImage = new Image(getResourceImage("pause").toURI().toString());
		Image playImage = new Image(getResourceImage("play").toURI().toString());
		
		if(musicPlayer.isPlaying()) {
			musicPlayer.pause();
			playPause.setImage(playImage);
		}else {
			musicPlayer.resume();
			playPause.setImage(pauseImage);
		}
	}
	
	@FXML
	private void musicPrev() {
		if(musicPlayer != null && currentMusic != null) {
			int index = getMusicIndex(currentMusic);
			playMusic(musicList.get(index - 1));
		}		
	}
	
	@FXML
	private void musicNext() {
		if(musicPlayer != null && currentMusic != null) {
			int index = getMusicIndex(currentMusic);
			playMusic(musicList.get(index + 1));
		}	
	}
	
	@FXML
	private void repeatChange() {
		isRepeat = (isRepeat) ? false : true;
		db.setConfig("repeat", String.valueOf(isRepeat));
		Image img;
		if(isRepeat) {
			 img = new Image(getResourceImage("repeat_one").toURI().toString());
		}else {
			 img = new Image(getResourceImage("repeat").toURI().toString());
		}
		repeat.setImage(img);
	}

	@FXML
	private void loadSearch() {
		loadFXMLSection("home_search");
	}
	
	@FXML
	private void loadHome() {
		loadFXMLSection("home_home");
	}
	
	@FXML
	private void loadSettings() {
		loadFXMLSection("home_settings");
	}

	@FXML
	private void loadContact() {
		loadFXMLSection("home_contact");
	}
	
	private static void statMusicNext() {
		if(musicPlayer != null && currentMusic != null) {
			int index = getMusicIndex(currentMusic);
			playMusic(musicList.get(index + 1));
		}	
	}
	
	public static void seek(int timestamp) {
//		===Calculate slider progress percent by timestamp(Second)
		double percent = ((timestamp / new FilesHelper().getMusicDuration(currentMusic.getPath())) * 100);
		progress.setValue(percent);
	}

	public static void seekPercent(int percent) {
		progress.setValue((double) percent);
	}

	public static void playMusic(Music music) {
//		===Stop playing music and set MusicPlayer to null
		musicPlayer.clear();
//		===Initialize MusicPlayer
		musicPlayer.setMusic(music);
//		===Play Music
		musicPlayer.play();
//		===Add listener for seeking music from Slider
		progress.valueProperty().addListener((obs, oldVal, newVal) -> {
			double old = oldVal.doubleValue();
			double nw = newVal.doubleValue();
			if ((nw - old) > 2 || (old - nw) > 2) {
				musicPlayer.seek(nw);
			}
		});
//		===Set current music
		currentMusic = music;
//		===Disable Button's if music in start or end of the list
		int index = getMusicIndex(currentMusic);
		statPrev.setVisible((index == 0) ? false : true);
		statNext.setVisible((index >= musicList.size() -1) ? false : true);
//		===Change playPause button image
		Image pauseImage = new Image(getResourceImage("pause").toURI().toString());
		statPlayPause.setImage(pauseImage);
//		===Set duration
		statCurrentDuration.setText(currentMusic.getDuration());
//		===Set current music metadata
		statCurrentTitle.setText(currentMusic.getTitle());
		statCurrentAuthor.setText(currentMusic.getAuthor());
//		===Set current music image
		File file = new File(currentMusic.getPath());
		Media media = new Media(file.toURI().toString());
		ObservableMap<String, Object> metadata = media.getMetadata();
		Image image = new Image(getResourceImage("logo").toURI().toString());
		statCurrentImage.setImage(image);
		metadata.addListener( (MapChangeListener<String, Object>) ch -> {
			if(ch.wasAdded()) {
				String key = ch.getKey();
				Object value = ch.getValueAdded();
				if(key.equals("image")) {
					System.out.println("Called");
						statCurrentImage.setImage((Image)value);
				}
			}
		});
	}
	
//	===Set music list
	public static void setMusicList(List<Music> musics) {
		musicList = musics;
	}
//	===Get music index in the list
	public static int getMusicIndex(Music music) {
		int index = 0;
		for(Music m : musicList) {
			if(currentMusic.getId() == m.getId()) {
				break;
			}else {
				index++;
			}
		}
		return index;
	}
	
	public static File getResourceImage(String name) {
		try {
//			===TODO Uncomment it for release
//			File file = new File(System.getProperty("user.dir") + "/resources/image/" + name + ".png");
			File file = new File(App.class.getResource("image/" + name + ".png").toURI());
			return file;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		}
	}
	
	public static void musicEnd() {
		if(isRepeat) {
			playMusic(currentMusic);
		}else {
			statMusicNext();
		}
	}
	
	public static void changeCurrentPosition(double timestamp) {
		int sec = (int) timestamp % 60;
		int min = (int) timestamp / 60;
		String second = (sec < 10) ? "0" + sec : String.valueOf(sec);
		String minute = (min < 10) ? "0" + min : String.valueOf(min);
		statCurrentPosition.setText(minute + ":" + second);
	}
	
	public static  void loadFXMLSection(String name) {
		try {
			Node node = (Node) App.loadFXML(name);
			statHomePane.getChildren().setAll(node);
		} catch (IOException e) {
			jlogger.exception(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
