package com.topichood.bo;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.topichood.dao.TopicHelper;
import com.topichood.dao.TweetHelper;
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
		int size = 5;
		String str = "2011-05-09 11:49:45";
		Timestamp time = new Timestamp(System.currentTimeMillis());
		time = Timestamp.valueOf(str);
		
		int topicId = 8;
		String s = "2015-01-01 0:0:0";
		String e = "2015-01-30 0:0:0";
		Timestamp start = new Timestamp(System.currentTimeMillis());
		start = Timestamp.valueOf(s);
		Timestamp end = new Timestamp(System.currentTimeMillis());
		end = Timestamp.valueOf(e);
		
		JSONObject backEndData = new JSONObject();
		TopicHelper topichelper = new TopicHelper();
		JSONArray tags = new JSONArray();
		List<Topic> topicList = topichelper.getTopic(time, size);
		long total = 0;
		for(Topic topic : topicList){
			total+=topic.getVolume();
		}
		System.out.println(total);
		
		for(Topic topic : topicList){
			JSONObject jsonObject = JSONObject.fromObject(topic);
			String proportion = getPercent(topic.getVolume(),total);
			proportion = proportion.substring(0, proportion.length()-1);
			jsonObject.element("proportion", proportion);
			tags.add(jsonObject);
		}
		
		TweetHelper tweethelper = new TweetHelper();
		JSONArray data = new JSONArray();
		for(int i=0;i<tags.size();i++){
			JSONObject o = new JSONObject();
			JSONObject tag = tags.getJSONObject(i);
			o.element("tag", tag.get("name"));
			o.element("proportion", tag.get("proportion"));
			JSONArray tweets = new JSONArray();
			List<Tweet> tweetList = tweethelper.getTweet(start, end, Integer.parseInt(tag.getString("id")));
			tweets = JSONArray.fromObject(tweetList);
			o.element("tweets", tweets);
			data.add(o);
		}
		backEndData.element("tags", tags);
		backEndData.element("data", data);
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
		   //DecimalFormat df1 = new DecimalFormat("0.00%");    //##.00%   百分比格式，后面不足2位的用0补齐  
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
