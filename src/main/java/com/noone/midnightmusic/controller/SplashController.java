package com.noone.midnightmusic.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import com.noone.midnightmusic.App;
import javafx.fxml.Initializable;


public class SplashController implements Initializable{

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new SplashTimer().start();
		
	}

}

class SplashTimer extends Thread{

	@Override
	public void run() {
		try {
//			===Set 3 second timer for Splash
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					try {
						App.setRoot("home");
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}, 3000);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}