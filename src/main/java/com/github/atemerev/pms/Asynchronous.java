package com.github.atemerev.pms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark listener method with this annotation, and it will be executed in the
 * DispatchListener's executor instead of the current thread.
 *
 * @author Alexander Temerev
 * @version $Id:$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Asynchronous {
}
