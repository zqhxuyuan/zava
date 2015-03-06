package io.qdb.kvstore.test

import io.qdb.kvstore.KeyValueStore
import io.qdb.kvstore.KeyValueStoreBuilder
import io.qdb.kvstore.RegexFilenameFilter
import org.apache.commons.io.FileUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Saving and loading snapshots.
 */
@Stepwise
class SnapshotSpec extends Specification {

    @Shared File baseDir = new File("build/test-snapshots")
    @Shared FilenameFilter filter = new RegexFilenameFilter(".+\\.snapshot")

    private KeyValueStore<String, ModelObject> createStore(File dir, boolean nuke = true) {
        if (nuke && dir.exists() && dir.isDirectory()) FileUtils.deleteDirectory(dir)
        return new KeyValueStoreBuilder<Integer, ModelObject>()
                .dir(dir)
                .alias("moo", ModelObject.class)
                .versionProvider(new VersionProvider())
                .create()
    }

    def "saveSnapshot with no changes is NOP"() {
        File dir = new File(baseDir, "one")
        def store = createStore(dir)
        store.saveSnapshot()
        store.close()

        expect:
        dir.list(filter).length == 0
    }

    def "saveSnapshot"() {
        File dir = new File(baseDir, "one")
        def store = createStore(dir)
        store.getMap("widgets").put("1", new ModelObject("one"))
        store.saveSnapshot()
        store.close()

        expect:
        dir.list(filter).length == 1
    }

    def "loadSnapshot on startup"() {
        def store = createStore(new File(baseDir, "one"), false)
        def widgets = store.getMap("widgets")
        def sz = widgets.size()
        def one = widgets.get("1")
        store.close()

        expect:
        sz == 1
        one instanceof ModelObject
        one.name == "one"
        one.version == 1
    }

    def "replay tx log on startup"() {
        File dir = new File(baseDir, "three")
        def store = createStore(dir)
        store.getMap("widgets").put("1", new ModelObject("one"))
        store.close()

        store = createStore(dir, false)
        def widgets = store.getMap("widgets")
        def sz = widgets.size()
        def one = widgets.get("1")
        store.close()

        expect:
        sz == 1
        one instanceof ModelObject
        one.name == "one"
        one.version == 1
    }
}
