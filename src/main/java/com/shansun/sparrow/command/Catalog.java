package com.shansun.sparrow.command;

import java.util.Iterator;

/**
 * ÃüÁîÄ¿Â¼
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-4
 */
public interface Catalog {

	void addCommand(String name, Command command);

	Command getCommand(String name);

	Iterator<Command> getCommands();

	Iterator<String> getNames();
}