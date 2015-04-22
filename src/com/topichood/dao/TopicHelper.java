package com.topichood.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.topichood.vo.Topic;
import com.topichood.dbc.Dbcon;

public class TopicHelper {
	private Dbcon dbc=new Dbcon();
    private Connection conn=dbc.getConnection();
    private Statement st=null;
    private ResultSet rs=null;
    
    public List<Topic> getHotTopic(Timestamp from,Timestamp to, int size, String neighbors) {
    	ArrayList<Topic> topics = new ArrayList<Topic>();
    	try {
    		st = conn.createStatement();
//			String sql = "SELECT tag_id, tag, count(tag) FROM tweet_tags t, tweet_tags_r r, "
//					+ "tweets ts where ts.neighborhood in ("+neighbors+") And t.id = r.tag_id And r.tweet_id = ts.tweet_id And ts.created_at between ' "+from+" ' "
//					+ "and '"+to+"' " + "group by tag order By count(tag) DESC limit "+size;
    		String sql = "select g.id, g.tag,count(g.tag) from tweets t, tweet_tags g, tweet_tags_r r where t.neighborhood in ("+neighbors+")and t.created_at between '"+from+"' and '"+to+"' and r.tweet_id = t.tweet_id and r.`tag_id` = g.`id` group by g.tag order by count(tweet_text) DESC limit "+size;

			//System.out.println(sql);
			//st.setTimestamp(1, latest);
			//st.setInt(2, size);
			rs = st.executeQuery(sql);
			while(rs.next()){
				Topic p = new Topic(rs.getInt("g.id"), rs.getString("g.tag"), rs.getInt("count(g.tag)"));
				topics.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return topics;
    }
    
    public List<Topic> getTopicList(Timestamp from,Timestamp to) throws SQLException{
    	List<Topic> tlist = new ArrayList<Topic>();
    	String sql = "select g.id, g.tag,count(g.tag) from tweets t, tweet_tags g, tweet_tags_r r where r.tweet_id = t.tweet_id and r.`tag_id` = g.`id` group by g.tag order by count(g.tag) desc";
    	st = conn.createStatement();
    	rs = st.executeQuery(sql);
    	while(rs.next()){
    		System.out.println(rs.getInt("count(g.tag)"));
    		if(rs.getInt("count(g.tag)") < 90){
    			continue;
    		}
    		Topic t = new Topic(rs.getInt("g.id"),rs.getString("g.tag"),rs.getInt("count(g.tag)"));
    		tlist.add(t);
    	}
    	System.out.println(tlist.size());
    	return tlist;
    }
    
    public List<Topic> getTopicById(Timestamp from,Timestamp to, String neighbors,String[] ids){
    	List<Topic> tlist = new ArrayList<Topic>();
    	try {
			st = conn.createStatement();
			for(int i=0; i<ids.length; i++){
	    		String sql = "select g.id, g.tag,count(g.tag) from tweets t, tweet_tags g, tweet_tags_r r where t.neighborhood in ("+neighbors+")and t.created_at between '"+from+"' and '"+to+"' and g.id = '"+ids[i]+"' and r.tweet_id = t.tweet_id and r.`tag_id` = g.`id` group by g.tag";
	    		rs = st.executeQuery(sql);
	    		if(rs.next()){
	    			Topic t = new Topic(rs.getInt("g.id"),rs.getString("g.tag"),rs.getInt("count(g.tag)"));
	    			tlist.add(t);
	    		}
	    	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return tlist;
    }
    
    public List<String> getRelatedTopic(String tagId){
    	List<String> list = new ArrayList<String>();
    	try {
			st = conn.createStatement();
			String sql = "select tag2 from related_tags where tag1 = '"+tagId+"' and times > 800";
			rs = st.executeQuery(sql);
			while(rs.next()){
				list.add(rs.getString("tag2"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return list;	
    }
    
    public int getRelationValue(String tag1, String tag2){
    	int value = 0;
    	try {
			st = conn.createStatement();
			String sql = "select times from related_tags where tag1 = '"+tag1+"' and tag2 = '"+tag2+"'";
			rs = st.executeQuery(sql);
			if(rs.next()){
				if(rs.getInt("times") > 800){
					value = rs.getInt("times");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return value;
    }
    
    public void closeConn(){
    	try {
			//rs.close();
			st.close();
	    	conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
}
