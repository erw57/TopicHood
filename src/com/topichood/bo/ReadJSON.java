/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.topichood.bo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 *
 * @author wanger
 */
public class ReadJSON {
    public static JSONObject readJson(HttpServletRequest request){
        JSONObject jo = new JSONObject();
        try {
            int amtread;
            char[] cbuf = new char[200];
            StringBuffer invalue = new StringBuffer();            
            BufferedReader in = request.getReader();
            while ((amtread = in.read(cbuf)) != -1) {
                invalue.append(cbuf, 0, amtread);
            }
            jo = JSONObject.fromObject(invalue.toString());
            
        } catch (IOException ex) {
            Logger.getLogger(ReadJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }
}
