package com.noone.midnightmusic;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;

import com.noone.midnightmusic.database.Database;


public class App extends Application {

    private static Scene scene;
    private static Stage stg;
    private static Parent parent;
    
 
    @Override 
    public void start(Stage stage) throws IOException {
    	Database db = new Database();
//    	===Initialize configuration
    	db.initConfig();
    	
        scene = new Scene(loadFXML("splash"), 1200, 800);
        stage.setResizable(true);
//        ===Remove WindowBorder
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
//        ===Set Application icon
        stage.getIcons().add(new Image(App.class.getResourceAsStream("image/logo.png")));
//        ===Set Application title
        stage.setTitle("Midnight Music");
        
        stage.setScene(scene);
        stage.show();
        stg = stage;
//		===Make window movable
        parent.setOnMousePressed(pressEvent -> {
        	parent.setOnMouseDragged(dragEvent -> {
        		stg.setX(dragEvent.getSceneX() - pressEvent.getSceneX());
        		stg.setY(dragEvent.getSceneY() - pressEvent.getSceneY());
        	});
        });
    }

    public static void minimize() {
    	stg.setIconified(true);
    }
    
    public static void exit() {
    	System.exit(0);
    }
    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        parent = fxmlLoader.load();
        return parent;
    }

    public static void main(String[] args) {
        launch();
    }

}