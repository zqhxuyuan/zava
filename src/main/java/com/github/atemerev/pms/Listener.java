package com.github.atemerev.pms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to mark listener methods. A listener method should have
 * a single argument (a message to handle) 
 *
 * @author Alexander Temerev
 * @version $Id$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
}
