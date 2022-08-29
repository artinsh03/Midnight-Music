package com.noone.midnightmusic.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.noone.midnightmusic.database.Database;
import com.noone.midnightmusic.model.Music;
import com.noone.midnightmusic.model.Path;
import com.noone.midnightmusic.util.FilesHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class HomeHomeController implements Initializable {

	@FXML
	private ListView<Music> musicList;
	
	@FXML
	private Label loading;
	
	@FXML
	private Pane emptyPane;
	
	private ObservableList<Music> data;
	
	private Database db;
	

	public HomeHomeController() {
		data = FXCollections.observableArrayList();
		db = new Database();	
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loading.setVisible(true);
		musicList.setVisible(false);
		emptyPane.setVisible(false);
		FilesHelper files = new FilesHelper();
		List<Path> paths = db.getPaths();
//		===Check if cached music's are available or not
		if (db.isMusicCacheEmpty()) {
			new Runnable() {
				@Override
				public void run() {
//					===Load music's from directory	
					for (Path path : paths) {
						for (File f : files.getMusicFiles(path.getPath())) {
							Music music = new Music();
							db.cacheMusic(music.fileToMusic(f , path.getId()));
							data.add(music.fileToMusic(f , path.getId()));
						}
					}
					data.addAll(db.getCacheMusics());
					
				}
			}.run();
		} else {
//			===Load music's from cached music's in database
			data.addAll(db.getCacheMusics());
		}
//		===Pass Data to HomeController
		HomeController.setMusicList(data);
		loading.setVisible(false);

		if(data.size() > 0) {
			musicList.setItems(data);
			musicList.setCellFactory(listView -> new ListItemController());
			musicList.setVisible(true);
		}else {
			emptyPane.setVisible(true);
		}
		
	}
	
	@FXML
	private void loadSettings() {
		HomeController.loadFXMLSection("home_settings");
	}
}

