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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ch.maxant.blog.nio.servlet3.model.Subscriber;

@WebListener
public class ModelInitialiser implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {

		// **************************
		// stick a new model into the application scope
		// **************************

		ServletContext appScope = sce.getServletContext();
		final Map<String, List<Subscriber>> clients = Collections.synchronizedMap(new HashMap<String, List<Subscriber>>());
		appScope.setAttribute(LoginServlet.CLIENTS, clients);
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
