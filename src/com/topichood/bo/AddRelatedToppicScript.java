package com.topichood.bo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.topichood.dbc.Dbcon;

public class AddRelatedToppicScript {
	
	private static class Pair{
		private int tag1;
		private int tag2;
		
		public void setTag1(int tag){
			this.tag1 = tag;
		}
		public void setTag2(int tag){
			this.tag2 = tag;
		}
		public int getTag1(){
			return this.tag1;
		}
		public int getTag2(){
			return this.tag2;
		}
	}

	public static void main(String[] args) {
		Dbcon dbc=new Dbcon();
	    Connection conn=dbc.getConnection();
	    Statement st=null;
	    Statement st2=null;
	    Statement st3=null;
	    ResultSet rs=null;
	    ResultSet rs2 = null;
	    
	    try {
			st = conn.createStatement();
			String sql = "select tweet_id from tweet_tags_r"; // get all tweets
			rs = st.executeQuery(sql);
			while(rs.next()){
				st2 = conn.createStatement();
				String sql2 = "select tag_id from tweet_tags_r where tweet_id = "+rs.getLong("tweet_id"); //get one id
				rs2 = st2.executeQuery(sql2);
				// all tags in one tweet
				List<Integer> tagIds = new ArrayList<Integer>();
				while(rs2.next()){
					tagIds.add(rs2.getInt("tag_id"));
				}
				if(tagIds.size()<=1){
					continue;
				}
				else{
					List<Pair> pairs = combine(tagIds); //get combinations
					for(int i=0; i<pairs.size(); i++){
						Pair p = pairs.get(i);
						String sql3 = "insert into topichood.related_tags values('"+p.getTag1()+"','"+p.getTag2()+"',1) on duplicate key update times = times+1";
						st3 = conn.createStatement();
						System.out.println("tag1: "+p.getTag1()+" tag2: "+p.getTag2());
						st3.executeUpdate(sql3);
					}
				}
				
			}
			rs.close();
		    rs2.close();
		    st.close();
		    st2.close();
		    st3.close();
		    conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	}
	
	private static List<Pair> combine(List<Integer> tagIds){
		List<Pair> plist = new ArrayList<Pair>();
		for(int i=0; i<tagIds.size(); i++){
			for(int j=0; j<tagIds.size(); j++){
				if(i==j){
					continue;
				}
				else{
					Pair p = new Pair();
					p.setTag1(tagIds.get(i));
					p.setTag2(tagIds.get(j));
					plist.add(p);
				}
			}
		}
		return plist;
	}

}
