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
    private String message=null;
    
    public List<Topic> getTopic(Timestamp latest,int size) {
    	ArrayList<Topic> topics = new ArrayList<Topic>();
    	try {
    		st = conn.createStatement();
			String sql = "SELECT tag_id, tag, count(tag) FROM tweet_tags t, tweet_tags_r r, "
					+ "tweets ts where t.id = r.tag_id And r.tweet_id = ts.tweet_id And ts.created_at > ' "+latest+" ' "
					+ "group by tag order By count(tag) DESC limit "+size;
			//st.setTimestamp(1, latest);
			//st.setInt(2, size);
			rs = st.executeQuery(sql);
			while(rs.next()){
				Topic p = new Topic(rs.getInt("tag_id"), rs.getString("tag"), rs.getInt("count(tag)"));
				topics.add(p);
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return topics;
    }
}
