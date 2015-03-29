package com.topichood.vo;

import java.sql.Timestamp;

public class Tweet {
	private String id;
	private float lat;
	private float lng;
	private String topic;
	private String createdAt;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}

