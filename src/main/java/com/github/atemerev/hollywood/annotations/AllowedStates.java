package com.github.atemerev.hollywood.annotations;

import com.github.atemerev.hollywood.RootState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This is documentation-scope annotation used to mark states from which actor methods are allowed to
 * be called. This annotation is not checked in any way, so keep it up to date.
 *
 * @author Alexander Temerev
 * @version $Id$
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface AllowedStates {
    Class<? extends RootState>[] value();
}
