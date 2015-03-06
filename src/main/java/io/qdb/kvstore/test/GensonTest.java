package io.qdb.kvstore.test;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqhxuyuan on 15-3-4.
 */
public class GensonTest {

    public static void main(String[] args) throws Exception{
        Genson genson = new Genson();
        testJavaPOJO(genson);
    }

    public static void testJavaPOJO(Genson genson) throws Exception{
        // read from a String, byte array, input stream or reader
        Person person = genson.deserialize("{\"age\":28,\"name\":\"Foo\"}", Person.class);

        String json = genson.serialize(person);
        // or produce a byte array
        byte[] jsonBytes = genson.serializeBytes(person);
        // or serialize to a output stream or writer
        OutputStream outputStream = new FileOutputStream("/home/hadoop/data/genson.txt");
        genson.serialize(person, outputStream);

        System.out.println(person);

        // will be deserialized to a list of maps
        List<Object> persons = genson.deserialize("[{\"age\":28,\"name\":\"Foo\"}]", List.class);
        // will produce same result as
        Object persons2 = genson.deserialize("[{\"age\":28,\"name\":\"Foo\"}]", Object.class);
    }

    public static void testJavaCollection(Genson genson) throws Exception{
        Map<String, Object> person = new HashMap<String, Object>() {{
            put("name", "Foo");
            put("age", 28);
        }};

        // {"age":28,"name":"Foo"}
        String singlePersonJson = genson.serialize(person);
        // will contain a long for the age and a String for the name
        Map<String, Object> map = genson.deserialize(singlePersonJson, Map.class);
    }

    public static void testDeserialize(Genson genson) throws Exception{
        String json = "[{\"age\":28,\"name\":\"Foo\"}]";

        List<Person> persons = genson.deserialize(json, new GenericType<List<Person>>(){});

        // or lets say we want to use something else than String as the keys of our Map.
        Map<Integer, Object> map = genson.deserialize(
                "{\"1\":28, \"2\":\"Foo\"}",
                new GenericType<Map<Integer, Object>>(){}
        );
    }

}
