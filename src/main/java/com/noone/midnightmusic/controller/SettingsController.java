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
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class SettingsController implements Initializable{

	@FXML
	private ListView<String> folderList;
	
	@FXML
	private ListView<String> musicList;
	
	private ObservableList<String> folderData;
	
	private ObservableList<String> musicData;
	
	private Database db;
	
	private FilesHelper files;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
			files = new FilesHelper();
			db = new Database();
			folderData = FXCollections.observableArrayList();
			musicData = FXCollections.observableArrayList();
			List<Path> folders = db.getPaths();
			for(Path path : folders) {
				folderData.add(path.getPath());
			}
			folderList.setItems(folderData);
			
			List<Music> musics = db.getNonFolderedMusic();
			for(Music music : musics) {
				musicData.add(music.getPath());
			}
			musicList.setItems(musicData);
	}
	
	@FXML
	private void removeFolder() {
		String selected = folderList.getSelectionModel().getSelectedItem();
		folderData.remove(selected);
		db.deleteMusicByFolder(selected);
		db.removePath(selected);
		
	}
	
	@FXML
	private void addFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		File file = chooser.showDialog(folderList.getScene().getWindow());
		if(file != null) {
			db.addPath(file.getAbsolutePath());
			for(File f : files.getMusicFiles(file.getAbsolutePath())) {
				Music music = new Music().fileToMusic(f, db.searchFolder(file.getAbsolutePath()).getId());
				db.cacheMusic(music);
			}
			folderList.getItems().add(file.getAbsolutePath());
		}
		
	}
	
	@FXML
	private void addMusic() {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(musicList.getScene().getWindow());
		if(file != null && file.getName().endsWith(".mp3")) {
			Music music = new Music().fileToMusic(file, 0);
			db.cacheMusic(music);
			musicList.getItems().add(music.getPath());
		}
	}
	@FXML
	private void removeMusic() {
		String selected = musicList.getSelectionModel().getSelectedItem();
		musicData.remove(selected);
		db.removeNonFolderedMusic(selected);
	}
	

}
