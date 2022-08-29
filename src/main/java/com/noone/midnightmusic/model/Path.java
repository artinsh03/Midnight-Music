package com.noone.midnightmusic.model;

import java.sql.ResultSet;

public class Path {

	private int id;
	private String path;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public Path resultSetToPath(ResultSet result) {
		try {
			Path path = new Path();
			path.setId(result.getInt("id"));
			path.setPath(result.getString("path"));
			return path;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
