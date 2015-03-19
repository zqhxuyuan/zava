package com.github.atemerev.hollywood.future;

import com.github.atemerev.pms.Message;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public class CancelledEvent implements Message {

    private Promise promise;

    public CancelledEvent(Promise promise) {
        this.promise = promise;
    }

    public Promise getPromise() {
        return promise;
    }
}