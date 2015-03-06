/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.LogRecord;

public class LogFormatter extends java.util.logging.Formatter
{
    public synchronized String format(LogRecord record)
    {
        StringBuilder recordBuffer = new StringBuilder();
        StringBuilder recordLabel = new StringBuilder();
        // Timestamp
/*
        now.setTime(record.getMillis());
        StringBuffer timestamp = new StringBuffer();
        Formatter timeFormatter = new Formatter(timestamp);
        timeFormatter.format(timeFormat, now);
*/
        long timestamp = record.getMillis();
        // Level
        recordLabel.append(record.getLevel().getLocalizedName());
        recordLabel.append(' ');
        // Source location
        StackTraceElement caller = caller();
        recordLabel.append(caller.getClassName());
        recordLabel.append('.');
        recordLabel.append(caller.getMethodName());
        recordLabel.append('(');
        recordLabel.append(caller.getFileName());
        recordLabel.append(':');
        recordLabel.append(caller.getLineNumber());
        recordLabel.append(") ");
        // Message
        StringTokenizer tokenizer = new StringTokenizer(formatMessage(record), "\n");
        int lineCount = 0;
        while (tokenizer.hasMoreTokens()) {
            if (lineCount > 0) {
                recordBuffer.append('\n');
            }
            recordBuffer.append(timestamp);
            recordBuffer.append(String.format(" %07d.%03d ", record.getSequenceNumber(), lineCount));
            recordBuffer.append(recordLabel);
            recordBuffer.append(tokenizer.nextToken());
            lineCount++;
        }
        // Stack
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                tokenizer = new StringTokenizer(sw.toString(), "\n");
                while (tokenizer.hasMoreTokens()) {
                    recordBuffer.append('\n');
                    recordBuffer.append(timestamp);
                    recordBuffer.append(String.format(" %07d.%03d ", record.getSequenceNumber(), lineCount));
                    recordBuffer.append(recordLabel);
                    recordBuffer.append(tokenizer.nextToken());
                    lineCount++;
                }
            } catch (Exception ex) {
            }
        }
        recordBuffer.append('\n');
        return recordBuffer.toString();
    }

    public LogFormatter()
    {
    }

    private StackTraceElement caller()
    {
        StackTraceElement frame;
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        int i = 0;
        do {
            frame = stack[i++];
        } while (ignoreFrame(frame));
        return stack[--i];
    }

    private static boolean ignoreFrame(StackTraceElement frame)
    {
        String className = frame.getClassName();
        return
            className.equals(THIS_CLASS_NAME) ||
            className.startsWith(JAVA_LOGGING_PACKAGE_NAME);
    }

    private static final String THIS_CLASS_NAME = LogFormatter.class.getName();
    private static final String JAVA_LOGGING_PACKAGE_NAME = "java.util.logging.";
    private static final String timeFormat = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL";

    private final Date now = new Date();
}
