package com.github.believe3301.nonheapdb.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ByteBufferTest.class,
        CacheConcurrentTest.class,
		CacheTest.class,
        VarintTest.class
})
public class AllTests {

}
