package com.topichood.vo;

public class Topic {
	
	private int id;
	private String name;
	private int volume;
	
	public Topic(int id, String name, int volume){
		this.id = id;
		this.name = name;
		this.volume = volume;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
}

