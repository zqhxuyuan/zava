package com.shansun.sparrow.command;


/**
 * 处理部件
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-3
 */
public interface Command {

	public boolean canExecute(Context context) throws CommandException;

	public void execute(Context context) throws CommandException;
	
	public void redo() throws CommandException;

	public void undo() throws CommandException;
}
