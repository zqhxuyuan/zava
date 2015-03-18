package com.shansun.sparrow.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-4
 */
public abstract class AbstractCatalog implements Catalog {

	protected Map<String, Command>	commands	= Collections.synchronizedMap(new HashMap<String, Command>());

	@Override
	public void addCommand(String name, Command command) {
		commands.put(name, command);
	}

	@Override
	public Command getCommand(String name) {
		return ((Command) commands.get(name));
	}

	@Override
	public Iterator<Command> getCommands() {
		return (commands.values().iterator());
	}

	@Override
	public Iterator<String> getNames() {
		return (commands.keySet().iterator());
	}
	
	public abstract void init();
}
