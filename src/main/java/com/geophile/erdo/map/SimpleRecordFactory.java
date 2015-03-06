/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.RecordFactory;

public class SimpleRecordFactory extends RecordFactory
{
    @Override
    public AbstractKey newKey()
    {
        try {
            return keyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FactoryException(e);
        }
    }

    @Override
    public AbstractRecord newRecord()
    {
        try {
            return recordClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FactoryException(e);
        }
    }

    public SimpleRecordFactory(Class<? extends AbstractKey> keyClass,
                               Class<? extends AbstractRecord> recordClass)
    {
        this.keyClass = keyClass;
        this.recordClass= recordClass;
    }

    private final Class<? extends AbstractKey> keyClass;
    private final Class<? extends AbstractRecord> recordClass;

    public static class FactoryException extends RuntimeException
    {
        FactoryException(Throwable throwable)
        {
            super(throwable);
        }
    }
}
