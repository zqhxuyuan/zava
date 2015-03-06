package io.qdb.kvstore.test

import io.qdb.kvstore.KeyValueStore
import io.qdb.kvstore.KeyValueStoreBuilder
import io.qdb.kvstore.OptimisticLockingException
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Stepwise
import org.apache.commons.io.FileUtils
import java.util.concurrent.ConcurrentMap

/**
 * Basic map tests.
 */
@Stepwise
class MapApiSpec extends Specification {

    @Shared KeyValueStore<String, ModelObject> store
    @Shared ConcurrentMap<String, ModelObject> widgets
    @Shared ConcurrentMap<String, ModelObject> bugs
    @Shared Listener listener = new Listener()

    static class Listener implements KeyValueStore.Listener<String, ModelObject> {
        List<KeyValueStore.ObjectEvent<String, ModelObject>> events = []
        void onObjectEvent(KeyValueStore.ObjectEvent<String, ModelObject> ev) { events << ev }
    }

    def setupSpec() {
        def dir = new File("build/test-basic")
        if (dir.exists() && dir.isDirectory()) FileUtils.deleteDirectory(dir)
        store = new KeyValueStoreBuilder<Integer, ModelObject>()
                .dir(dir)
                .alias("moo", ModelObject.class)
                .versionProvider(new VersionProvider())
                .listener(listener)
                .create()
        widgets = store.getMap("widgets", ModelObject)
        bugs = store.getMap("bugs")
    }

    def cleanupSpec() {
        store.close()
    }

    def setup() {
        listener.events.clear()
    }

    def "isEmpty"() {
        expect:
        store.isEmpty()
        widgets.isEmpty()
        widgets.size() == 0
    }

    def "put and get"() {
        def put = widgets.put("1", new ModelObject("one"))
        def get = widgets.get("1")
        def none = bugs.get("1")
        def ev = listener.events.first()
        println("ev = " + ev)

        expect:
        !store.isEmpty()
        !widgets.isEmpty()
        widgets.size() == 1
        put == null
        get.name == "one"
        get.version == 1
        none == null
        store.mapNames == ["widgets"]
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.CREATED
        ev.key == "1"
        ev.value.name == "one"
    }

    def "opt lock check on put"() {
        when:
        widgets.put("1", new ModelObject("one"))

        then:
        thrown(OptimisticLockingException)
    }

    def "putIfAbsent"() {
        def ans1 = widgets.putIfAbsent("1", new ModelObject("onexx"))
        def get1 = widgets.get("1")
        def ans2 = widgets.putIfAbsent("2", new ModelObject("two"))
        def get2 = widgets.get("2")
        def ev = listener.events.first()
        println("ev = " + ev)

        expect:
        ans1.name == "one"
        get1.name == "one"
        ans2 == null
        get2.name == "two"
        get2.version == 1
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.CREATED
        ev.key == "2"
        ev.value.name == "two"
    }

    def "replace"() {
        def ans1 = widgets.replace("1", new ModelObject("onexx", 1))
        def get1 = widgets.get("1")
        def ans3 = widgets.replace("3", new ModelObject("three"))
        def get3 = widgets.get("3")
        def ev = listener.events.first()

        expect:
        ans1.name == "one"
        get1.name == "onexx"
        get1.version == 2
        ans3 == null
        get3 == null
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.UPDATED
        ev.key == "1"
        ev.value.name == "onexx"
    }

    def "replace oldValue newValue"() {
        def ans0 = widgets.replace("1", new ModelObject("onex"), new ModelObject("one"))
        def ans1 = widgets.replace("1", new ModelObject("onexx"), new ModelObject("one"))
        def get1 = widgets.get("1")
        def ev = listener.events.first()

        expect:
        !ans0
        ans1
        get1.name == "one"
        get1.version == 1
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.UPDATED
        ev.key == "1"
        ev.value.name == "one"
    }

    def "remove"() {
        def ans = widgets.remove("2")
        def get = widgets.get("2")
        def ev = listener.events.first()
        println("ev = " + ev)

        expect:
        ans.name == "two"
        get == null
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.DELETED
        ev.key == "2"
        ev.value.name == "two"
    }

    def "remove value opt lock check"() {
        when:
        widgets.put("2", new ModelObject("two", 22))
        widgets.remove("2", new ModelObject("two"))

        then:
        thrown(OptimisticLockingException)
    }

    def "remove value"() {
        def ans0 = widgets.remove("2", new ModelObject("twox", 23))
        def ans2 = widgets.remove("2", new ModelObject("two", 23))
        def get = widgets.get("2")
        def ev = listener.events.first()

        expect:
        !ans0
        ans2
        get == null
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.DELETED
        ev.key == "2"
        ev.value.name == "two"
    }

    def "keySet"() {
        def keys = widgets.keySet().asList()
        def none = bugs.keySet()

        expect:
        keys == ["1"]
        none.isEmpty()
    }

    def "values"() {
        def values = widgets.values().asList()
        def none = bugs.values()

        expect:
        values.size() == 1
        values[0].name == "one"
        none.isEmpty()
    }

    def "entrySet"() {
        def es = widgets.entrySet().asList()
        def none = bugs.entrySet()

        expect:
        es.size() == 1
        es[0].key == "1"
        es[0].value.name == "one"
        none.isEmpty()
    }

    def "size"() {
        def sz = widgets.size()
        def none = bugs.size()

        expect:
        sz == 1
        none == 0
    }

    def "containsKey"() {
        def one = widgets.containsKey("1")
        def two = widgets.containsKey("2")
        def none = bugs.containsKey("1")

        expect:
        one
        !two
        !none
    }

    def "containsValue"() {
        def one = widgets.containsValue(new ModelObject("one"))
        def two = widgets.containsValue(new ModelObject("two"))
        def none = bugs.containsValue(new ModelObject("two"))

        expect:
        one
        !two
        !none
    }

    def "clear"() {
        widgets.clear()
        widgets.clear()     // to cover the null map condition
        def ev = listener.events.first()

        expect:
        widgets.isEmpty()
        store.mapNames == []
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.DELETED
        ev.key == "1"
        ev.value.name == "one"
    }

    def "putAll"() {
        Map<String, ModelObject> map = [:]
        map.put("1", new ModelObject("one"))
        widgets.putAll(map)
        def ev = listener.events.first()

        expect:
        widgets.size() == 1
        widgets.values().iterator().next().name == "one"
        listener.events.size() == 1
        ev.store == store
        ev.map == "widgets"
        ev.type == KeyValueStore.ObjectEvent.Type.CREATED
        ev.key == "1"
        ev.value.name == "one"
    }

}
