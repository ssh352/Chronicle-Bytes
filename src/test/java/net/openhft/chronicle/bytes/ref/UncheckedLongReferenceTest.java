/*
 * Copyright 2016 higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.bytes.ref;

import net.openhft.chronicle.bytes.NativeBytesStore;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Created by Peter Lawrey on 20/12/16.
 */
public class UncheckedLongReferenceTest {
    @Test
    public void test() {
        @NotNull UncheckedLongReference ref = new UncheckedLongReference();
        @NotNull NativeBytesStore<Void> nbs = NativeBytesStore.nativeStoreWithFixedCapacity(32);
        ref.bytesStore(nbs, 16, 8);
        assertEquals(0, ref.getValue());
        ref.addAtomicValue(1);
        assertEquals(1, ref.getVolatileValue());
        ref.addValue(-2);
        assertEquals("value: -1", ref.toString());
        assertFalse(ref.compareAndSwapValue(0, 1));
        assertTrue(ref.compareAndSwapValue(-1, 2));
        assertEquals(8, ref.maxSize());
        assertEquals(nbs.addressForRead(16), ref.offset());
        try {
            assertEquals(nbs, ref.bytesStore());
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        assertEquals(0L, nbs.readLong(0));
        assertEquals(0L, nbs.readLong(8));
        assertEquals(2L, nbs.readLong(16));
        assertEquals(0L, nbs.readLong(24));

        ref.setValue(10);
        assertEquals(10L, nbs.readLong(16));
        ref.setOrderedValue(20);
        Thread.yield();
        assertEquals(20L, nbs.readLong(16));
        nbs.releaseLast();
    }
}