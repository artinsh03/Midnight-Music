package com.noone.midnightmusic.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.noone.midnightmusic.database.Database;
import com.noone.midnightmusic.model.Music;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class SearchController implements Initializable{

	@FXML
	private ListView<Music> searchList;
	
	@FXML
	private Label searchEmpty;
	
	@FXML
	private TextField searchInput;
	
	private Database db;
	
	
	
	public SearchController() {
		db = new Database();
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchList.setVisible(false);
		searchEmpty.setVisible(false);
		searchList.setCellFactory(listView -> new ListItemController());
		
		searchInput.textProperty().addListener( (obs , oldVal , newVal) -> {
			if(newVal.length() > 0) {
				List<Music> data = db.searchMusic(newVal);	
				if(data.size() > 0) {
					searchList.setVisible(true);
					searchEmpty.setVisible(false);			
					searchList.getItems().clear();
					searchList.getItems().addAll(data);
					
				}else {
					searchList.setVisible(false);
					searchEmpty.setVisible(true);
					
				}
			}else {
				searchList.getItems().clear();
				searchList.setVisible(false);
				searchEmpty.setVisible(false);
			}	
		});
	}
	
	
}
