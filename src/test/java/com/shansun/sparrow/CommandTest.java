package com.shansun.sparrow;

import junit.framework.TestCase;

import org.junit.Test;

import com.shansun.sparrow.command.AbstractCommand;
import com.shansun.sparrow.command.CommandException;
import com.shansun.sparrow.command.Context;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public class CommandTest extends TestCase {

	class SimpleCommand extends AbstractCommand {

		@Override
		public boolean canExecute(Context context) throws CommandException {
			return context != null; // 检查入参，绝对是否接受参数
		}

		@Override
		public void doExecute(Context context) throws CommandException {
			System.out.println(context);
			context.addProperty("return", "success");
		}

		@Override
		public void redo() throws CommandException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void undo() throws CommandException {
			throw new UnsupportedOperationException();
		}
	}

	class SimpleContext extends Context {
	}

	@Test
	public void test() throws CommandException {
		CommandTest test = new CommandTest();

		Context ctx = test.new SimpleContext();
		test.new SimpleCommand().execute(ctx);

		System.out.println(ctx.getProperty("return"));
	}
}
