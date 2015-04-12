package com.topichood.bo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.topichood.dbc.Dbcon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class addNeighborhoodScript {

	public static void main(String[] args) {
//        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=40.4297662949258,-79.9763488769531&sensor=true";
//        String json = loadJSON(url);
//        JSONObject obj = new JSONObject();
//        obj = JSONObject.fromObject(json);
//        String name =((JSONArray) obj.get("results")).getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("short_name");
//        JSONObject addCompo = results.getJSONObject(0);
//        JSONArray compo = addCompo.getJSONArray("address_components");
//        JSONObject compo0 = compo.getJSONObject(2);
//        String name = compo0.getString("short_name");
        //System.out.println(name);
        
        Dbcon dbc=new Dbcon();
	    Connection conn=dbc.getConnection();
	    Statement st=null;
	    Statement st2=null;
	    ResultSet rs=null;
	    
	    try {
			st = conn.createStatement();
			String sql = "select * from tweets order by tweet_id";
	    	rs = st.executeQuery(sql);
	    	long i = 0;
	    	while(rs.next()){
	    		Thread.sleep(400);
	    		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+rs.getFloat("geo_lat")+","+rs.getFloat("geo_long")+"&sensor=true";
	            String json = loadJSON(url);
	            JSONObject obj = new JSONObject();
	            obj = JSONObject.fromObject(json);
	            System.out.println(rs.getString("tweet_id")+"---"+(i++)+"----"+obj.toString());
	            String name =((JSONArray) obj.get("results")).getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("short_name");
	            System.out.println(name);
	    		st2 = conn.createStatement();
	    		String sql2 = "Update tweets set neighborhood = '"+name+"' where tweet_id = '"+rs.getString("tweet_id")+"'";
	            st2.executeUpdate(sql2);
	    	}
		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
    }
	
	public void conncetDB(){
		
	}
 
    public static String loadJSON (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                        yc.getInputStream()));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }

}
