/*
 * Copyright 2012 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.buffer;

import java.util.Random;

class Msg {
    long id;
    long timestamp;
    String routingKey;
    byte[] payload;

    public Msg(long timestamp, Random rnd, int maxPayloadSize) {
        this.timestamp = timestamp;
        routingKey = "key" + timestamp;
        payload = new byte[rnd.nextInt(maxPayloadSize + 1)];
        rnd.nextBytes(payload);
    }

    Msg(long timestamp, String routingKey, byte[] payload) {
        this.timestamp = timestamp;
        this.routingKey = routingKey;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "id:" + id + " timestamp:" + timestamp;
    }
}
