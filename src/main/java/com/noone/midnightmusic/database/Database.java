package com.noone.midnightmusic.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.noone.jlogger.JLogger;
import com.noone.midnightmusic.App;
import com.noone.midnightmusic.model.Music;
import com.noone.midnightmusic.model.Path;

public class Database {

	private Connection connection;
	private Statement statement;
	private JLogger jlogger;

	public Database() {
		jlogger = new JLogger();
	}

	private boolean openConnection() {
		try {
//			===TODO Uncomment it for release
//			connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "/data.db");
			connection = DriverManager.getConnection("jdbc:sqlite:" + App.class.getResource("database/data.db"));
			statement = connection.createStatement();
			return true;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return false;
		}
	}

	private boolean closeConnection() {
		try {
			if (connection != null && statement != null) {
				statement.close();
				connection.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Path> getPaths() {
		try {
			openConnection();
			String query = "SELECT * from music_path";
			List<Path> data = new ArrayList<>();
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				data.add(new Path().resultSetToPath(result));
			}
			return data;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		} finally {
			closeConnection();
		}

	}

	

	public boolean isMusicCacheEmpty() {
		try {
			openConnection();
			ResultSet result = statement.executeQuery("SELECT id from music_cache");
			if (result.next()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return true;
		} finally {
			closeConnection();
		}
	}

	public boolean cacheMusic(Music music) {
		try {
			openConnection();
			String query = "INSERT INTO music_cache VALUES(null,'" + music.getTitle() + "' , '" + music.getAuthor()
			+ "' , '" + music.getPath() + "' , '" + music.getDuration() + "' , " + music.getFolder() + ")";
	return statement.execute(query);
		}catch(Exception e){
			jlogger.exception(e.getMessage());
			return false;
		}finally {
			closeConnection();
		}	
	}

	public List<Music> getCacheMusics() {
		try {
			openConnection();
			List<Music> data = new ArrayList<>();
			String query = "SELECT * FROM music_cache";
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				data.add(new Music().resultSetToMusic(result));
			}
			return data;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		} finally {
			closeConnection();
		}
	}

	public boolean isConfigExists() {
		try {
			openConnection();
			String query = "SELECT id FROM config";
			ResultSet result = statement.executeQuery(query);
			return (result.next()) ? true : false;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return true;
		}finally {
			closeConnection();
		}
	}

	public void initConfig() {	
		try {
			openConnection();
			if (!isConfigExists()) {
				String repeatQuery = "INSERT INTO config VALUES(null,'repeat','true');";
				statement.execute(repeatQuery);
			}
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
		}finally {
			closeConnection();
		}
	}

	public Map<String, String> getConfig() {
		try {
			openConnection();
			String query = "SELECT * FROM config";
			ResultSet result = statement.executeQuery(query);
			Map<String, String> data = new HashMap<>();
			while (result.next()) {
				data.put(result.getString("key"), result.getString("value"));
			}
			return data;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		} finally {
			closeConnection();
		}
	}

	public boolean setConfig(String key, String value) {		
		try {
			openConnection();
			String query = "UPDATE config SET value = '" + value + "' WHERE key = '" + key + "'";
			return statement.execute(query);
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			return false;
		}finally {
			closeConnection();
		}				
	}

	public List<Music> searchMusic(String value) {
		try {
			openConnection();
			String query = "SELECT DISTINCT * FROM music_cache WHERE title LIKE '%" + value + "%' OR author LIKE '%"
					+ value + "%'";
			ResultSet result = statement.executeQuery(query);
			List<Music> data = new ArrayList<>();
			while (result.next()) {
				data.add(new Music().resultSetToMusic(result));
			}
			return data;
		} catch (Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		} finally {
			closeConnection();
		}
	}

	public boolean deleteMusicByFolder(String folder) {
		try {
			Path path = searchFolder(folder);
			openConnection();
			String query = "DELETE FROM music_cache WHERE folder = " + path.getId();
			return statement.execute(query);
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			e.printStackTrace();
			return false;
		}finally {
			closeConnection();
		}
		
	}
	
	public Path searchFolder(String path) {
		try {
			openConnection();
			String query = "SELECT * FROM music_path WHERE path = '" + path + "'";
			ResultSet result = statement.executeQuery(query);
			if(result.next()) {
				return new Path().resultSetToPath(result);
			}else {
				return null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			jlogger.exception(e.getMessage());
			return null;
		}finally {
			closeConnection();
		}
	}
	public boolean removePath(String path) {
		try {
			openConnection();
			String query = "DELETE FROM music_path WHERE path = '" + path + "'";
			return statement.execute(query);
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			return false;
		}finally {
			closeConnection();
		}
		
	}
	
	public boolean addPath(String path) {
		try {
			openConnection();
			String query = "INSERT INTO music_path VALUES(null , '" + path + "');";
			return statement.execute(query);
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			return false;
		}finally {
			closeConnection();
		}
	}
	
	public List<Music> getNonFolderedMusic(){
		try {
			openConnection();
			String query = "SELECT * FROM music_cache WHERE folder = 0";
			ResultSet result = statement.executeQuery(query);
			List<Music> data = new ArrayList<>();
			while(result.next()) {
				data.add(new Music().resultSetToMusic(result));
			}
			return data;
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			return null;
		}finally {
			closeConnection();
		}
		
	}
	
	public Music searchNonFolderedMusic(String path) {
		try {
			openConnection();
			String query = "SELECT * FROM music_cache WHERE path = '" + path + "' AND folder = 0";
			ResultSet result = statement.executeQuery(query);
			if(result.next()) {
				return new Music().resultSetToMusic(result);
			}else {
				return null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			jlogger.exception(e.getMessage());
			return null;
		}finally {
			closeConnection();
		}
	}
	
	public boolean removeNonFolderedMusic(String path) {
		try {
			openConnection();
			String query = "DELETE FROM music_cache WHERE path = '" + path + "' AND folder = 0";
			return statement.execute(query);
		}catch(Exception e) {
			jlogger.exception(e.getMessage());
			return false;
		}finally {
			closeConnection();
		}
	}

}
