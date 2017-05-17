/*  
 * Copyright (c) 2011 Ant Kutschera
 * 
 * This file is part of Ant Kutschera's blog, 
 * http://blog.maxant.co.uk
 * 
 * This is free software: you can redistribute
 * it and/or modify it under the terms of the
 * Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * It is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the Lesser GNU General Public License for
 * more details. 
 * 
 * You should have received a copy of the
 * Lesser GNU General Public License along with this software.
 * If not, see http://www.gnu.org/licenses/.
 */
package ch.maxant.blog.nio.servlet3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.maxant.blog.nio.servlet3.model.Subscriber;

@WebServlet(name = "loginServlet", urlPatterns = { "/login" }, asyncSupported = true)
public class LoginServlet extends HttpServlet {

	public static final String CLIENTS = "ch.maxant.blog.nio.servlet3.clients";

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// dont set the content length in the response, and we will end up with chunked 
		// encoding so that a) we can keep the connection open to the client, and b) send
		// updates to the client as chunks.
		
		// *********************
		// we use asyncSupported=true on the annotation for two reasons. first of all, 
		// it means the connection to the client isn't closed by the container.  second 
		// it means that we can pass the asyncContext to another thread (eg the publisher) 
		// which can then send data back to that open connection.
		// so that we dont require a thread per client, we also use NIO, configured in the 
		// connector of our app server (eg tomcat)
		// *********************

		// what channel does the user want to subscribe to?  
		// for production we would need to check authorisations here!
		String channel = request.getParameter("channel");

		// ok, get an async context which we can pass to another thread
		final AsyncContext aCtx = request.startAsync(request, response);

		// a little longer than default, to give us time to test.
		// TODO if we use a heartbeat, then time that to pulse at a similar rate
		aCtx.setTimeout(20000L); 

		// create a data object for this new subscription
		Subscriber subscriber = new Subscriber(aCtx, channel);

		// get the application scope so that we can add our data to the model
		ServletContext appScope = request.getServletContext();

		// fetch the model from the app scope
		@SuppressWarnings("unchecked")
		Map<String, List<Subscriber>> clients = (Map<String, List<Subscriber>>) appScope.getAttribute(CLIENTS);

		// add a listener so we can remove the subscriber from the model in 
		// case of completion or errors or timeouts
		aCtx.addListener(new AsyncListener("login", clients, channel, subscriber));

		// *********************
		// now add this subscriber to the list of subscribers per channel.  
		// *********************

		// restrict access to other sessions momentarily, so that this app
		// scoped model has the channel only put into it only one time
		synchronized (clients) {
			List<Subscriber> subscribers = clients.get(channel);
			if(subscribers == null){
				// first subscriber to this channel!
				subscribers = Collections.synchronizedList(new ArrayList<Subscriber>());
				clients.put(channel, subscribers);
			}
			
			// add out data object to the model
			subscribers.add(subscriber);
		}

		// acknowledge the subscription
		aCtx.getResponse().getOutputStream().print("hello - you are subscribed to " + channel);
		aCtx.getResponse().flushBuffer(); //to ensure the client gets this ack NOW
	}
}
