/*
 * Copyright 2008 Niels Peter Strandberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.Allogy.simplemq.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import com.github.Allogy.simplemq.Utils;

public class TestUtils {


    @Test(expected = IllegalArgumentException.class)
    public void testSerialize() {
        ArrayList<Bar> list = new ArrayList<Bar>();
        list.add(new Bar());
        Utils.serialize(list);
    }


    @Test
    public void testCopy() {
        Foo f = new Foo();
        Foo copy = (Foo) Utils.copy(f);
        assertNotSame(f, copy);
    }


    @Test
    public void testDeletedirectory() throws IOException {

        String tmp = System.getProperty("java.io.tmpdir");

        File dir = new File(tmp, "testdir");
        dir.mkdir();

        File file = new File(dir, "filename");
        file.createNewFile();

        Utils.deleteDirectory(dir);
        assertFalse(dir.exists());
    }
}

class Foo implements Serializable {

}

class Bar {

}
