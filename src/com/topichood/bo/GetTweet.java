package com.topichood.bo;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.topichood.dao.TweetHelper;
import com.topichood.vo.Tweet;



/**
 * Servlet implementation class getTweets
 */
@WebServlet("/GetTweet")
public class GetTweet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTweet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int topicId = 8;
		String s = "2015-01-01 0:0:0";
		String e = "2015-01-30 0:0:0";
		Timestamp start = new Timestamp(System.currentTimeMillis());
		start = Timestamp.valueOf(s);
		Timestamp end = new Timestamp(System.currentTimeMillis());
		end = Timestamp.valueOf(e);
		
		TweetHelper tweethelper = new TweetHelper();
		List<Tweet> tweetList = tweethelper.getTweet(start, end, topicId);
		JSONArray array = new JSONArray();
		//OutputStream outputStream = new FileOutputStream("geodata2");
		//Writer       writer       = new OutputStreamWriter(outputStream);
		for(Tweet t : tweetList){
			JSONObject jsonObject = JSONObject.fromObject(t);
			//writer.write(jsonObject.toString() + "," + '\n');
			array.add(jsonObject);
		}
		System.out.print(array.size());
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write(array.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
