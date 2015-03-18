package base;

import java.util.HashMap;

import static org.junit.Assert.assertNull;

/**
 * Created by zqhxuyuan on 15-3-17.
 */
public class TestCol {

    public static void main(String[] args) {

    }

    public static void testPutMap(){
        HashMap<String, byte[]> maps = new HashMap<>();
        String key = "key";
        byte[] value = "value".getBytes();
        assertNull(maps.put(key, value));
    }
}
