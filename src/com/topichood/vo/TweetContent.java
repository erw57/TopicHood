package com.topichood.vo;

import java.sql.Timestamp;

public class TweetContent {
	private String id;
	private String text;
	private String createdAt;
	private String author;
	private String image_url;
	
	public void setId(String id){
		this.id = id;
	}
	public void setText(String text){
		this.text = text;
	}
	public void setCreatedAt(Timestamp time){
		this.createdAt = time.toString();
	}
	public void setAuthor(String author){
		this.author = author;
	}
	public void setImgUrl(String url){
		this.image_url = url;
	}
	
	public String getId(){
		return this.id;
	}
	public String getText(){
		return this.text;
	}
	public String getCreateAt(){
		return this.createdAt;
	}
	public String getAuthor(){
		return this.author;
	}
	public String getImgUrl(){
		return this.image_url;
	}
}
