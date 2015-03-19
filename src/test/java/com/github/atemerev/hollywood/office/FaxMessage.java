package com.github.atemerev.hollywood.office;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class FaxMessage {

    private String content;

    public FaxMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return content;
    }
}
