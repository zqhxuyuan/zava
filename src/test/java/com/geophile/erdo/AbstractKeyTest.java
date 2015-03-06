/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AbstractKeyTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testNewKey()
    {
        TestKey key = new TestKey(0);
        try {
            key.erdoId();
            assertTrue(false);
        } catch (AssertionError e) {
            // expected
        }
        try {
            key.deleted();
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testKeyMarkedDeleted()
    {
        TestKey key = new TestKey(0);
        key.deleted(true);
        try {
            key.erdoId();
            assertTrue(false);
        } catch (AssertionError e) {
            // expected
        }
        try {
            assertTrue(key.deleted());
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testKeyMarkedNotDeleted()
    {
        TestKey key = new TestKey(0);
        key.deleted(false);
        try {
            key.erdoId();
            assertTrue(false);
        } catch (AssertionError e) {
            // expected
        }
        try {
            assertTrue(!key.deleted());
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testKeyWithId()
    {
        TestKey key = new TestKey(0);
        key.erdoId(ERDO_ID);
        try {
            assertEquals(ERDO_ID, key.erdoId());
        } catch (AssertionError e) {
            assertTrue(false);
        }
        try {
            assertTrue(!key.deleted());
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testKeyWithIdMarkedDeleted()
    {
        TestKey key = new TestKey(0);
        key.erdoId(ERDO_ID);
        key.deleted(true);
        try {
            assertEquals(ERDO_ID, key.erdoId());
        } catch (AssertionError e) {
            assertTrue(false);
        }
        try {
            assertTrue(key.deleted());
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testKeyWithIdMarkedNotDeleted()
    {
        TestKey key = new TestKey(0);
        key.erdoId(ERDO_ID);
        key.deleted(false);
        try {
            assertEquals(ERDO_ID, key.erdoId());
        } catch (AssertionError e) {
            assertTrue(false);
        }
        try {
            assertTrue(!key.deleted());
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testMarkDeletedThenSetId()
    {
        TestKey key = new TestKey(0);
        key.deleted(true);
        key.erdoId(ERDO_ID);
        try {
            assertEquals(ERDO_ID, key.erdoId());
        } catch (AssertionError e) {
            assertTrue(false);
        }
        try {
            assertTrue(key.deleted());
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testMarkNotDeletedThenSetId()
    {
        TestKey key = new TestKey(0);
        key.deleted(false);
        key.erdoId(ERDO_ID);
        try {
            assertEquals(ERDO_ID, key.erdoId());
        } catch (AssertionError e) {
            assertTrue(false);
        }
        try {
            assertTrue(!key.deleted());
        } catch (AssertionError e) {
            assertTrue(false);
        }
    }

    private static final int ERDO_ID = 999;
    private static TestFactory FACTORY;
}
