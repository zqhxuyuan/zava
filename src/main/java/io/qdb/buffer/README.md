io.qdb.buffer
=============

https://github.com/qdb-io/qdb-buffer

Disk based message queue supporting sequential retrieval of old messages by id and timestamp. Designed to be embedded
in JVM applications. Much more efficient than storing messages in a relational database or in a MongoDB capped
collection or whatever.


Usage
-----

Creating a new buffer:

    MessageBuffer mb = new PersistentMessageBuffer("buffer-dir");
    mb.setMaxLength(100 * 1000 * 1000000L /*100G*/ );

The buffer will store its data in files in buffer-dir, each approximately 100M in size (100G / 1000). When the buffer
is full the oldest file(s) are deleted to make space.

Appending a message:

    byte[] message = ...
    long id = mb.append(System.currentTimeMillis(), "some:routing:information", message);
    System.out.println("Appended message id " + id);

Message id's always get bigger but are not sequential. Note that the routing key is saved with the message but is
not used by qdb-buffer.

Read messages:

    MessageCursor c = mb.cursor(0);
    while (c.next()) {
        System.out.println("id " + c.getId() + " timestamp " + c.getTimestamp() +
            " routing key [" + c.getRoutingKey() + "] payload size " + c.getPayloadSize());
        Byte[] payload = c.getPayload();
        ...
    }

You can call next() after it returns false to poll for new messages.

Read messages with blocking:

    MessageCursor c = mb.cursor(mb.getNextMessageId()); // read new messages
    while (c.next(10000)) {     // wait up to 10 secs for a message, use 0 to block forever
        ...
    }

Read messages by timestamp:

    long timestamp = new SimpleDateFormat("yyyyMMdd HH:mm").parse("20121113 05:47").getTime();
    MessageCursor c = mb.cursorByTimestamp(timestamp);
    while (c.next()) {
        // process the message
    }

Get a timeline of messages in the buffer. This can be used to create a user interface to read old messages from a
given time onwards and so on. There will be one entry on the timeline for each file in the buffer.

    Timeline t = mb.getTimeline();
    for (int i = 0, n = t.size(); i < n; i++) {
        System.out.println("id " + t.getMessageId(i) + " timestamp " + t.getTimestamp(i) + " bytes " + getBytes(i) +
            " millis " + t.getMillis(i) + " count " + t.getCount(i));
    }

Get a more detailed timeline for the file containing the message. This is used to "drill down" and provide
more detail for part of the bigger timeline.

    Timeline t = mb.getTimeline(messageId);


Features
--------

- Efficient reading of messages from any point in time or id
- High performance
- Automatic recovery after system crash. Some messages may be lost but the buffer will not be corrupt
- Buffer provides a timeline or histogram of messages over time (e.g. for creating a chart)
- Old messages are efficiently deleted when the buffer is full
- Low memory usage
- No dependencies


FAQ
---

### How is qdb-buffer different to message queuing middleware (e.g. RabbitMQ)?

Qdb-buffer is concerned with persisting messages to disk efficiently and replaying them from a point in time.
It does not keep messages in memory or perform message routing. It is designed to sit inside (between) a JVM application
generating messages and some other application (e.g. messaging middleware), likely running on a different machine,
that processes or routes them. If the remote application is down or there a network problem, messages are buffered
by the generating application and not lost. If the remote application loses a bunch of messages or processes them
incorrectly they can be resent.

### How does qdb-buffer achieve "high performance"?

Messages are appended to files on disk. A 4k block at the start of each file holds the expected size of the file
(for crash recovery) and a time based index for quick seeking. There is no need for a separate transaction log and
most writes are simple appends. Old messages are deleted by simply deleting message files.

### How does qdb-buffer achieve "low memory usage"?

Messages are not buffered in memory and written to disk sometime later. They go to disk right away. The memory
footprint is independent of the number of messages in the buffer.


Changelog
---------

0.4.1:
- Added getCreationTime
- Added caching for getMostRecentTimestamp for better performance

0.4.0:
- Added isEmpty and getMostRecentTimestamp
- Renamed some methods in MessageBuffer removing superflous "Message" in the names
- Added getMessageSize

0.3.3:
- Fixed rare NPE cursoring over buffer being appended to

0.3.2:
- Got rid of shutdown hook

0.3.1:
- Added MessageCursor.getNextId()

0.3.0:
- Renamed MessageBuffer setMaxLength, getMaxLength and getLength to setMaxSize, getMaxSize and getSize respectively
- Added setFirstMessageId to MessageBuffer and removed this parameter from PersistentMessageBuffer constructor
- Renamed MessageBuffer getOldestMessage to getOldestMessageDate
- Added MessageBuffer getOldestMessageId
- MessageBuffer.append now treats a null routingKey as empty string
- MessageBuffer.maxPayloadSize now defaults to 128k

0.2.0:
- Added ShutdownHook to close open buffers on VM shutdown
- Added isOpen, getMessageCount and getOldestMessage to MessageBuffer
- High level timeline now include message counts
- Closing a MessageBuffer will now interrupt threads waiting for new messages

0.1.0:
- Initial release


Building
--------

This project is built using Gradle (http://www.gradle.org/). Download and install Gradle (just unzip it and
make sure 'gradle' is on your path). Then do:

    $ gradle check
    $ gradle assemble
    $ gradle install

This will run the unit tests, create jars in build/libs and install them in your local maven repository.


License
-------

Copyright 2012 David Tinker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
