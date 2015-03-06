qdb-kvstore
===========

https://github.com/qdb-io/qdb-kvstore

Transactional in memory key/value store. Writes the store to disk periodically in snapshot files. Uses a transaction
log to replay from the last snapshot after a crash.

Fire's events when objects are created, updated or deleted.

Reading from the store is just about as fast as reading from java.util.ConcurrentHashMap.


Usage
-----

Create a KeyValueStore using a KeyValueStoreBuilder:

    KeyValueStore<Integer, ModelObject> store = new KeyValueStoreBuilder<Integer, ModelObject>()
        .dir(dir)
        .versionProvider(new VersionProvider())
        .listener(new ListenerImpl())
        .create();

    ConcurrentMap<Integer, ModelObject> widgets = store.getMap("widgets");
    widgets.put(1, new ModelObject("A widget"));
    ...
    ModelObject w = widgets.get(1);

In this case the keys are Integer's and the values are ModelObject's.

All parameters except dir are optional. The store keeps its snapshots and transaction log in dir. You need to supply
a version provider if you want to use optimistic locking.

Once you have a store you can get named maps and use them as you would a normal java.util.ConcurrentMap. However
all map methods might throw a KeyValueStoreException and all writes to the store are serialized. In addition if
you are using optimistic locking then replacing one value with another will only work if the incoming value has
the same version number as the existing value. If not an OptimisticLockingException is thrown.

It is important to remember that your actual objects are stored in the map. **They are not copied on put or get**.
So don't modify instances after putting them in or getting them from a map. A clone option may be added in future
for extra safety.

If optimistic locking is used then adding or replacing a value in a map will bump up its version. If you use
the ConcurrentMap putIfAbsent or replace methods then the incoming object will have its version incremented even if
it doesn't end up in the map. This is to prevent it from being in the map with an old version for any period of time.


Changelog
---------

0.1.2:
- Added file locking so multiple JVMs won't use the same store directory

0.1.1:
- Upgraded to qdb-buffer 0.4.0

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
