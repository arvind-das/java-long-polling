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
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncEvent;

import ch.maxant.blog.nio.servlet3.model.Subscriber;

public class AsyncListener implements javax.servlet.AsyncListener {

	private final String name;
	private final Map<String, List<Subscriber>> model;
	private final String channel;
	private final Subscriber subscriber;

	public AsyncListener(String name, Map<String, List<Subscriber>> model, String channel, Subscriber subscriber){
		this.name = name;
		this.model = model;
		this.channel = channel;
		this.subscriber = subscriber;
	}
	
	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onComplete for " + name);
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onTimeout for " + name);
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		removeFromModel();
		System.out.println("onError for " + name);
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		System.out.println("onStartAsync for " + name);
		if(event.getThrowable() != null) event.getThrowable().printStackTrace();
	}

	private void removeFromModel() {
		model.get(channel).remove(subscriber);
	}
	
}
