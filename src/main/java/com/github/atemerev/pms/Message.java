package com.github.atemerev.pms;

import java.io.Serializable;

/**
 * Marker interface for message objects (the ones that could be listened to).
 * All messages should be marked with this interface in order to be processed
 * in the DispatchListener.
 *
 * @author Alexander Temerev
 * @version $Id:$
 */
public interface Message extends Serializable {
}
