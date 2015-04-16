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
    
    public List<Topic> getTopic(Timestamp from,Timestamp to, int size, String neighbors) {
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
