package com.topichood.bo;

import java.io.IOException;
import java.sql.SQLException;
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

/**
 * Servlet implementation class GetTopicNeighborList
 */
@WebServlet("/GetTopicNeighborList")
public class GetTopicNeighborList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTopicNeighborList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject object = new JSONObject();
		TopicHelper topichelper = new TopicHelper();
		TweetHelper tweethelper = new TweetHelper();
		try {
			List<Topic> tlist = topichelper.getTopicList();
			JSONArray tArray = JSONArray.fromObject(tlist);
			List<String> neighbors = tweethelper.getNeighborList();
			object.element("topicList", tArray);
			object.element("neighborList", neighbors);
			topichelper.closeConn();
			tweethelper.closeConn();
			response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.getWriter().write(object.toString());
			//List<String> = 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
