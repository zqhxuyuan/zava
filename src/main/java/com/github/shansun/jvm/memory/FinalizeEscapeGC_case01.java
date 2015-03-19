package com.github.shansun.jvm.memory;

/**
 * GC时对象的自我拯救
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-12
 */
public class FinalizeEscapeGC_case01 {

	private static FinalizeEscapeGC_case01	SAVE_HOOK	= null;

	public void isAlive() {
		System.err.println("Yes, I'm still alive!");
	}

	public static void main(String[] args) throws InterruptedException {
		SAVE_HOOK = new FinalizeEscapeGC_case01();

		// 对象第一次成功自我拯救
		SAVE_HOOK = null;

		System.gc();

		// GC需要时间
		Thread.sleep(1000);

		if (SAVE_HOOK != null) {
			SAVE_HOOK.isAlive();
		} else {
			System.err.println("No, I'm dead!");
		}

		// 对象第二次自我拯救失败。Finalize只会执行一次
		SAVE_HOOK = null;

		System.gc();

		// GC需要时间
		Thread.sleep(1000);

		if (SAVE_HOOK != null) {
			SAVE_HOOK.isAlive();
		} else {
			System.err.println("No, I'm dead!");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.err.println("Finalize method executed!");
		FinalizeEscapeGC_case01.SAVE_HOOK = this;
	}
}
