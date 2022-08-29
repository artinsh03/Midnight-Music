package com.noone.midnightmusic.controller;

import com.noone.jlogger.JLogger;
import com.noone.midnightmusic.App;
import com.noone.midnightmusic.model.Music;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class ListItemController extends ListCell<Music> {

	private FXMLLoader loader;

	

	@FXML
	private Label title, artist, duration;

	@FXML
	private AnchorPane root;

	private Music music;

	private JLogger jlogger;


	public ListItemController() {
		jlogger = new JLogger();
		
	}

	@Override
	protected void updateItem(Music item, boolean empty) {
		super.updateItem(item, empty);
//		===Check if item is empty or not
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (loader == null) {
//				===Load FXML to item
				loader = new FXMLLoader(App.class.getResource("list_item.fxml"));
				loader.setController(this);
				try {
					loader.load();
					
				} catch (Exception e) {
					jlogger.exception(e.getMessage());
				}
			}

			try {
//				===Set music metadata from Music Object
				title.setText(item.getTitle());
				artist.setText(item.getAuthor());
				duration.setText(item.getDuration());
				
				setText(null);
				setGraphic(root);
//				===Set current item Music object to global Music object
				music = item;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	===Play music by clicking on item
	@FXML
	private void play() {
		HomeController.playMusic(music);
	}
}
