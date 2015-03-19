package com.zqh.classloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.zqh.classloader.v2.MyBean;
import org.junit.Test;

public class ClassLoaderTest {

    public static final String JARS_DIR = System.getProperty("user.dir") + "/src/test/resources";


    @Test
    public void testLocalClass() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {

        MyBean myBean = new MyBean();

        assertEquals("v2", myBean.toString());

        assertEquals("paolo.test.custom_classloader.support.MyBean", myBean
                .getClass().getName());

    }

    @Test
    public void testManuallyLoadedClass() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {

        DirectoryBasedParentLastURLClassLoader classLoader = new DirectoryBasedParentLastURLClassLoader(
                ClassLoaderTest.JARS_DIR	);
        Class<?> classManuallyLoaded = classLoader
                .loadClass("paolo.test.custom_classloader.support.MyBean");

        Object myBeanInstanceFromReflection = classManuallyLoaded.newInstance();

        Method methodToString = classManuallyLoaded.getMethod("toString");

        assertEquals("v1", methodToString.invoke(myBeanInstanceFromReflection));

    }

    @Test
    public void testDifferentClassloaders() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {

        MyBean myBean = new MyBean();

        DirectoryBasedParentLastURLClassLoader classLoader = new DirectoryBasedParentLastURLClassLoader(
                JARS_DIR);
        Class<?> classManuallyLoaded = classLoader
                .loadClass("paolo.test.custom_classloader.support.MyBean");

        Object myBeanInstanceFromReflection = classManuallyLoaded.newInstance();

        assertNotEquals(myBean.getClass(),
                myBeanInstanceFromReflection.getClass());

    }

    @Test
    public void testCannotCast() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        DirectoryBasedParentLastURLClassLoader classLoader = new DirectoryBasedParentLastURLClassLoader(
                JARS_DIR);
        Class<?> classManuallyLoaded = classLoader
                .loadClass("paolo.test.custom_classloader.support.MyBean");

        Object myBeanInstanceFromReflection = classManuallyLoaded.newInstance();

        try {
            MyBean myBean = (MyBean) myBeanInstanceFromReflection;
            fail("An exception was expected here, condition");
        } catch (ClassCastException e) {
            assertTrue("the expected exception has been raised", true);
        }
    }

    @Test
    public void testExtraMethod() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {

        DirectoryBasedParentLastURLClassLoader classLoader = new DirectoryBasedParentLastURLClassLoader(
                JARS_DIR);
        Class<?> classManuallyLoaded = classLoader
                .loadClass("paolo.test.custom_classloader.support.MyBean");

        Object myBeanInstanceFromReflection = classManuallyLoaded.newInstance();

        Method methodToString = classManuallyLoaded.getMethod("extraMehtod");

        assertEquals("extra_value",
                methodToString.invoke(myBeanInstanceFromReflection));
    }

}