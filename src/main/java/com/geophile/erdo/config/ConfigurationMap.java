/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.config;

import com.geophile.erdo.util.Array;
import com.geophile.erdo.util.IntArray;
import com.geophile.erdo.util.LongArray;

import java.io.*;
import java.util.*;

public class ConfigurationMap
{
    public void write(File file) throws IOException
    {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        try (PrintWriter output = new PrintWriter(file)) {
            for (String key : keys) {
                output.println(String.format("%s = %s", key, map.get(key)));
            }
        }
    }

    public void read(File file) throws IOException
    {
        map.clear();
        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = input.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "=");
                String key = tokenizer.nextToken();
                String value = tokenizer.nextToken();
                map.put(key.trim(), value.trim());
            }
        }
    }

    public synchronized Integer intValue(String key)
    {
        return Integer.parseInt(map.get(key));
    }

    public synchronized IntArray intArray(String key)
    {
        IntArray intList = new IntArray(null);
        StringTokenizer tokenizer = new StringTokenizer(map.get(key), ",");
        while (tokenizer.hasMoreTokens()) {
            intList.append(Integer.parseInt(tokenizer.nextToken()));
        }
        return intList;
    }

    public synchronized LongArray longArray(String key)
    {
        LongArray longList = new LongArray();
        StringTokenizer tokenizer = new StringTokenizer(map.get(key), ",");
        while (tokenizer.hasMoreTokens()) {
            longList.append(Long.parseLong(tokenizer.nextToken()));
        }
        return longList;
    }

    public synchronized Array<String> stringArray(String key)
    {
        Array<String> stringList = new Array<String>();
        StringTokenizer tokenizer = new StringTokenizer(map.get(key), ",");
        while (tokenizer.hasMoreTokens()) {
            stringList.append(tokenizer.nextToken());
        }
        return stringList;
    }

    public synchronized Long longValue(String key)
    {
        return Long.parseLong(map.get(key));
    }

    public synchronized Double doubleValue(String key)
    {
        return Double.parseDouble(map.get(key));
    }

    public void value(String key, Integer value)
    {
        map.put(key, value.toString());
    }

    public void value(String key, Long value)
    {
        map.put(key, value.toString());
    }

    public void value(String key, Double value)
    {
        map.put(key, value.toString());
    }

    public String value(String key)
    {
        return map.get(key);
    }

    public void value(String key, String value)
    {
        map.put(key, value);
    }

    public Map<String, String> toMap()
    {
        return map;
    }

    public ConfigurationMap copy()
    {
        return new ConfigurationMap(new HashMap<String, String>(map));
    }

    public ConfigurationMap()
    {
        map = new HashMap<String, String>();
    }

    private ConfigurationMap(Map<String, String> map)
    {
        this.map = map;
    }

    // State

    final Map<String, String> map;
}
