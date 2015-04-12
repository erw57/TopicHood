package com.topichood.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.topichood.dbc.Dbcon;
import com.topichood.vo.Tweet;

public class TweetHelper {
	private Dbcon dbc=new Dbcon();
    private Connection conn=dbc.getConnection();
    private Statement st=null;
    private ResultSet rs=null;
    private String message=null;
    
    public ArrayList<Tweet> getTweet(Timestamp start, Timestamp end, int topicId){
    	ArrayList <Tweet> tweets = new ArrayList<Tweet>();
    	try {
			st = conn.createStatement();
			String sql = "SELECT * FROM tweets t, tweet_tags_r r, tweet_tags w Where r.tweet_id = t.tweet_id And t.created_at between '" + start 
					+ "' And '" + end + "' And r.tag_id = " + topicId+" AND w.id = r.tag_id";
	    	
	    	rs = st.executeQuery(sql);
			while(rs.next()){
				Tweet t = new Tweet();
				t.setId(rs.getString("tweet_id"));
				t.setGeo(rs.getFloat("geo_lat"), rs.getFloat("geo_long"));
				//t.setLat(rs.getFloat("geo_lat"));
				//t.setLng(rs.getFloat("geo_long"));
				//t.setTopic(rs.getString("tag"));
				//t.setCreatedAt(rs.getString("created_at"));
				tweets.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
		return tweets;
	}
}
