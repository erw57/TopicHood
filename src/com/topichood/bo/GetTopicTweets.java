package com.topichood.bo;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.topichood.bo.GetTopicTweets;
import com.topichood.dao.TopicHelper;
import com.topichood.dao.TweetHelper;
import com.topichood.vo.Point;
import com.topichood.vo.Topic;
import com.topichood.vo.Tweet;

/**
 * Servlet implementation class GetTopicTweets
 */
@WebServlet("/GetTopicTweets")
public class GetTopicTweets extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTopicTweets() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//read json
//		JSONObject jo = new JSONObject();
//		jo = ReadJSON.readJson(request);
//		String timeSpan = jo.getString("time");
//		JSONArray neighborsjo = jo.getJSONArray("neighbors");
		
		String timeSpan = request.getParameter("time");
		String neighborsRaw = request.getParameter("neighborhood");
		System.out.println("nei:"+neighborsRaw);
		String topics = request.getParameter("topics");
		String neighbors = "'Downtown','Shadyside'";
		String[] topicIds = {"20"};
		if(topics != null && !topics.equals("null")){
			topicIds = topics.split(",");
		}	
		if(neighborsRaw != null && !neighborsRaw.equals("\'null\'")){
			neighbors = neighborsRaw.replace('+', ' ');
		}
		System.out.println(neighbors);
		
		//get time
		String timeStr = "";
		int unit;
		if(timeSpan == null){
			timeStr = "2015-01-13 0:0:0";
			unit = 60*60*1000; //one hour
		}
		else if(timeSpan.equals("day")){
			timeStr = "2015-01-13 0:0:0";
			unit = 60*60*1000;  //one hour
		}
		else if(timeSpan.equals("week")){
			timeStr = "2015-01-06 0:0:0";
			unit = 60*60*24*1000;  //one day
		}
		else{//month
			timeStr = "2015-01-01 0:0:0";
			unit = 60*60*24*1000; //one day
		}
		//top 5 topics
		int size = 5;
		Timestamp from = new Timestamp(System.currentTimeMillis());
		from = Timestamp.valueOf(timeStr);
		
		Timestamp to = new Timestamp(System.currentTimeMillis());
		to = Timestamp.valueOf("2015-01-14 0:0:0");
		
//		String s = "2015-02-01 0:0:0";
//		String e = "2015-01-3 0:0:0";
//		Timestamp start = new Timestamp(System.currentTimeMillis());
//		start = Timestamp.valueOf(s);
//		Timestamp end = new Timestamp(System.currentTimeMillis());
//		end = Timestamp.valueOf(e);
		
		JSONObject backEndData = new JSONObject();
		TopicHelper topichelper = new TopicHelper();
		TweetHelper tweethelper = new TweetHelper();
		JSONArray tags = new JSONArray();
		//get top 5 topics
		//List<Topic> topicList = topichelper.getTopic(from,to,size,neighbors);
		List<Topic> topicList = topichelper.getTopicById(from, to, neighbors, topicIds);
		long total = 0;
		for(Topic topic : topicList){
			total+=topic.getVolume();
		}
		
		for(Topic topic : topicList){
			System.out.println("Volume:"+topic.getName()+"----"+topic.getVolume());
			JSONObject jsonObject = JSONObject.fromObject(topic);
			//calculate proportion
			String proportion = getPercent(topic.getVolume(),total);
			proportion = proportion.substring(0, proportion.length()-1);
			jsonObject.element("proportion", proportion);
			//get points on chart
			List<Point> points = tweethelper.getTopicVolume(topic.getId(),from,to,unit,neighbors);
			JSONArray pointArray = JSONArray.fromObject(points);
			jsonObject.element("points", pointArray);
			tags.add(jsonObject);
		}
		
		JSONArray data = new JSONArray();
		for(int i=0;i<tags.size();i++){
			JSONObject o = new JSONObject();
			JSONObject tag = tags.getJSONObject(i);
			o.element("tag", tag.get("name"));
			o.element("proportion", tag.get("proportion"));
			JSONArray tweets = new JSONArray();
			List<Tweet> tweetList = tweethelper.getTweet(from, to, Integer.parseInt(tag.getString("id")),neighbors);
			tweets = JSONArray.fromObject(tweetList);
			System.out.println("tweets length:"+tag.get("name")+"-----"+tweets.size());
			o.element("tweets", tweets);
			data.add(o);
		}
		
		//related graph data
		List<String> nodes = new ArrayList<String>();
		for(int i=0; i<tags.size(); i++){
			nodes.add(tags.getJSONObject(i).getString("name"));
		}
		for(int i=0; i<tags.size(); i++){
			List<String> relates = topichelper.getRelatedTopic(tags.getJSONObject(i).getString("id"));
			for(int j=0; j<relates.size(); j++){
				if(!nodes.contains(relates.get(j))){ //not add the same tag
					nodes.add(relates.get(j));
				}
			}
		}
		JSONArray node = JSONArray.fromObject(nodes);
		// calculate relation
		JSONArray relation = new JSONArray();
		for(int i=0; i< nodes.size()-1; i++){
			for(int j=i+1; j<nodes.size(); j++){
				int value = topichelper.getRelationValue(nodes.get(i), nodes.get(j));
				if(value > 0){
					JSONObject object = new JSONObject();
					object.element("from", nodes.get(i));
					object.element("to", nodes.get(j));
					double k = (double) (value*1.0/10000);
					String result;
					if(k > 1){
						result = "1";
					}
					else{
						DecimalFormat df = new DecimalFormat("0.0");
						result = df.format(k);
					}
					
					object.element("value", result);
					relation.add(object);
				}
			}
		}
		
		
		JSONObject related = new JSONObject();
		related.element("nodes", node);
		related.element("relations", relation);
		//close db connection
		topichelper.closeConn();
		tweethelper.closeConn();
		System.out.println(related.toString());
		backEndData.element("tags", tags);
		backEndData.element("data", data);
		backEndData.element("related", related);
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(backEndData.toString());
	}
	
	public String getPercent(int x,long total){  
		   String result="";  
		   double x_double=x*1.0; 
		   double total_double=total*1.0; 
		   double tempresult=x_double/total_double;  
		   NumberFormat nf = NumberFormat.getPercentInstance(); 
		   nf.setMinimumFractionDigits( 2 );      
		   //DecimalFormat df1 = new DecimalFormat("0.00%");  
		   result=nf.format(tempresult);     
		   //result= df1.format(tempresult);    
		   return result;  
		}  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
