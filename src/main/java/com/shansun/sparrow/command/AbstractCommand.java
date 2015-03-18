package com.shansun.sparrow.command;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-4
 */
public abstract class AbstractCommand implements Command {

	public void execute(Context context) throws CommandException {
		if (canExecute(context)) {
			doExecute(context);
		}
	}

	public abstract void doExecute(Context context) throws CommandException;
}
