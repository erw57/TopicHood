package com.topichood.bo;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.topichood.dao.TopicHelper;
import com.topichood.vo.Topic;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Servlet implementation class HotTopics
 */
@WebServlet("/HotTopics")
public class HotTopics extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HotTopics() {
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
		
		TopicHelper topichelper = new TopicHelper();
		JSONArray array = new JSONArray();
		List<Topic> topicList = topichelper.getTopic(time, size);
		for(Topic topic : topicList){
			JSONObject jsonObject = JSONObject.fromObject(topic);
			array.add(jsonObject);
		}
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
