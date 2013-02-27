package com.salsaforce.freshsalsa;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Postable {
	public abstract void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
