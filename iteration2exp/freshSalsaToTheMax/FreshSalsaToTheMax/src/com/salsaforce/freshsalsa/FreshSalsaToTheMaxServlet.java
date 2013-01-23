package com.salsaforce.freshsalsa;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class FreshSalsaToTheMaxServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page prints out the key value pairs on the querystring line by line.");
		for (Object key : req.getParameterMap().keySet()) {
			if (key instanceof String) {
				String sKey = (String) key;
				resp.getWriter().println(sKey + " " + req.getParameter(sKey));
			}
		}
	}
}
