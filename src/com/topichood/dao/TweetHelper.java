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
import com.topichood.vo.Point;
import com.topichood.vo.Tweet;

public class TweetHelper {
	private Dbcon dbc=new Dbcon();
    private Connection conn=dbc.getConnection();
    private Statement st=null;
    private ResultSet rs=null;
    
    public ArrayList<Tweet> getTweet(Timestamp start, Timestamp end, int topicId, String neighbors){
    	ArrayList <Tweet> tweets = new ArrayList<Tweet>();
    	try {
			st = conn.createStatement();
//			String sql = "SELECT * FROM tweets t, tweet_tags_r r, tweet_tags w Where t.neighborhood IN ("+neighbors+") AND r.tweet_id = t.tweet_id And t.created_at between '" + start 
//					+ "' And '" + end + "' And w.id = " + topicId+" AND w.id = r.tag_id";
			String sql = "select * from tweets t, tweet_tags g, tweet_tags_r r where t.neighborhood in ("+neighbors+") and t.created_at between '"+start+"' and '"+end+"' and r.tweet_id = t.tweet_id and r.tag_id = g.id and g.id = "+topicId;

	    	rs = st.executeQuery(sql);
			while(rs.next()){
				Tweet t = new Tweet();
				t.setId(rs.getString("tweet_id"));
				t.setGeo(rs.getFloat("geo_lat"), rs.getFloat("geo_long"));
				t.setNei(rs.getString("neighborhood"));
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
    
    public int getTweetCount(Timestamp start, Timestamp end, int topicId, String neighbors){
    	int count = 0;
    	try {
//    		String sql = "SELECT count(*) FROM tweets t, tweet_tags_r r, tweet_tags g Where r.tweet_id = t.tweet_id And t.created_at between '" + start 
//    				+ "' and '" + end + "' And t.neighborhood in ("+neighbors+") And g.id = " + topicId+" and r.tag_id = g.id";
    		String sql = "select count(*) from tweets t, tweet_tags g, tweet_tags_r r where t.neighborhood in ("+neighbors+") and t.created_at between '"+start+"' and '"+end+"' and r.tweet_id = t.tweet_id and r.tag_id = g.id and g.id = "+topicId;
			//System.out.println(sql);
    		st = conn.createStatement();
    		rs = st.executeQuery(sql);
			if(rs.next()){
				count = rs.getInt("count(*)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return count;
    	
    }
    
    public List<Point> getTopicVolume(int topicId, Timestamp from, Timestamp to, int unit, String neighbors){
    	List<Point> points = new ArrayList<Point>();
    	int x = 1;
    	for(long mark = from.getTime();mark < to.getTime();mark+=unit,x++){
			Point point  = new Point();
			point.setX(x);
			point.setY(getTweetCount(new Timestamp(mark), new Timestamp(mark + unit), topicId, neighbors));
			points.add(point);
		}
    	return points;
    }
    
    public void closeConn(){
    	try {
			rs.close();
			st.close();
	    	conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
}
