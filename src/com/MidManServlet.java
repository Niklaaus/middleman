package com;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class MidManServlet
 */
public class MidManServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MidManServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = "https://en-gb.facebook.com";
		String urlType = request.getParameter("urlType");
		String temp = request.getParameter("requestedUrl");
		if (temp != null && "relative".equals(urlType)) {
			url = url + temp;
		} else if (temp != null) {
			url = temp;
		}
		HttpClient httpclient = HttpClients.createDefault();

		HttpGet httpget = new HttpGet(url);
		HttpSession session = request.getSession();
		BasicCookieStore cookieStore = null;
		if (session.getAttribute("cookieStore") == null) {
			cookieStore = new BasicCookieStore();
		} else {
			cookieStore = (BasicCookieStore) session
					.getAttribute("cookieStore");
		}
		session.setAttribute("cookieStore", cookieStore);
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		HttpResponse responseFromRemoteHost = httpclient.execute(httpget,
				httpContext);
		HttpEntity entity = responseFromRemoteHost.getEntity();

		InputStream instream = null;
		if (entity != null) {
			instream = entity.getContent();

			InputStreamReader readFromRemoteServer = new InputStreamReader(
					instream, "UTF-8");

			StringWriter writer = new StringWriter();
			IOUtils.copy(readFromRemoteServer, writer);
			String theString = writer.toString();
			System.out.println("hiiii reached here>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			request.setAttribute("responseBody", theString);

			instream.close();
			RequestDispatcher rd = request
					.getRequestDispatcher("/Container.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String requestedUrl = request.getParameter("requestedUrl");
		String formData = request.getParameter("formData");
		
		
		String url = "https://en-gb.facebook.com";
		String urlType = request.getParameter("urlType");
		if (requestedUrl != null && "relative".equals(urlType)) {
			url = url + requestedUrl;
		} else if (requestedUrl != null) {
			url = requestedUrl;
		}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost(url);

		// Request parameters and other properties.
		Type collectionType = new TypeToken<Collection<Map>>() {
		}.getType();
		Collection<Map> mapOfFormData = new Gson().fromJson(formData,
				collectionType);

		if (mapOfFormData != null) {
			for (Map map : mapOfFormData) {
				params.add(new BasicNameValuePair((String) map.get("name"),
						(String) map.get("value")));

			}

			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		}
		
		System.out.println("\n\n\nurl:"+url);
		System.out.println("\n"+params+"\n\n\n\n\n\n");
		HttpContext httpContext = new BasicHttpContext();
		HttpSession session = request.getSession();
		BasicCookieStore cookieStore = (BasicCookieStore) session
				.getAttribute("cookieStore");
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		// Execute and get the response.
		HttpResponse responseFromRemoteHost = null;

		if (mapOfFormData != null) {
			responseFromRemoteHost = httpclient.execute(httppost, httpContext);
		} else {
			HttpGet httpget = new HttpGet(requestedUrl);
			responseFromRemoteHost = httpclient.execute(httpget);

		}
		HttpEntity entity = responseFromRemoteHost.getEntity();

		InputStream instream = null;
		if (entity != null) {
			instream = entity.getContent();

			InputStreamReader readFromRemoteServer = new InputStreamReader(
					instream, "UTF-8");
			instream.close();
			StringWriter writer = new StringWriter();
			IOUtils.copy(readFromRemoteServer, writer);
			String theString = writer.toString();

			request.setAttribute("responseBody", theString);
			RequestDispatcher rd = request
					.getRequestDispatcher("/Container.jsp");
			rd.forward(request, response);

		}

	}
}
