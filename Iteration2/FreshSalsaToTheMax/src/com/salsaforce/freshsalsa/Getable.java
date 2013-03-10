package com.salsaforce.freshsalsa;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Getable {
	public abstract void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
