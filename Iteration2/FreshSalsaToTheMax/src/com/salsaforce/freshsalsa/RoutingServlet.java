package com.salsaforce.freshsalsa;

import java.io.IOException;

import javax.servlet.http.*;

/**
 * This is the sole servlet for the Salsalytics Google App Engine Server.
 * It routes requests to the proper handlers.
 * @author Cory Karras
 * @version 1.0
 */
public class RoutingServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5898604888742461428L;
	private static final String thisPackage = "com.salsaforce.freshsalsa";
	
	/** Handles GET requests.
	 * @param req The request to the sever.
	 * @param resp The response to the request.
	 * @throws IOException if println messes up.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String className = req.getRequestURI().substring(1);
		
		Getable instance = getInstance(className, Getable.class, resp);
		if (instance != null) {
			instance.doGet(req, resp);
		}
	}
	
	/** Handles POST requests.
	 * @param req The request to the sever.
	 * @param resp The response to the request.
	 * @throws IOException if println messes up.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String className = req.getRequestURI().substring(1);
		
		Postable instance = getInstance(className, Postable.class, resp);
		if (instance != null) {
			instance.doPost(req, resp);
		}
	}
	
	/**
	 * Gets and instance of the requested class if it is of the given type.
	 * @param className The short name of the class. Note that this method only works for classes inside this package.
	 * @param requestType The type that the requested class should be an instance of.
	 * @param resp The response to the current request.
	 * @return An instance of the requested class if it is valid, otherwise null.
	 * @throws IOException This should only be thrown if println fails.
	 */
	private <T> T getInstance(String className, Class<T> requestType, HttpServletResponse resp) throws IOException {
		try {
			Class<?> classObj = Class.forName(thisPackage + "." + className);
			Object instance = classObj.getConstructors()[0].newInstance();
			if (requestType.isAssignableFrom(instance.getClass())) {
				return requestType.cast(instance);
			}
			else {
				resp.sendError(404);
				resp.getWriter().println("Page " + className + " not found: (Invalid Class)");
			}
		}
		catch (ClassNotFoundException exc) {
			resp.sendError(404);
			resp.getWriter().println("Page " + className + " not found: (Class not found)");
		}
		catch (NoClassDefFoundError err) {
			resp.sendError(404);
			resp.getWriter().println("Page " + className + " not found: (Class name mismatch: Caps)");
		}
		catch (IOException exc) {
			throw exc;
		}
		catch (Exception exc) {
			//If it gets here there is a problem with the reflection code.
			resp.sendError(404);
			resp.getWriter().println("Page " + className + " not found: (reflection error)");
		}
		return null;
	}
}
