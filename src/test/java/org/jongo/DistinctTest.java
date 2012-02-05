/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
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

package org.jongo;

import org.jongo.model.Coordinate;
import org.jongo.model.Poi;
import org.jongo.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static org.jongo.util.TestUtil.createEmptyCollection;
import static org.jongo.util.TestUtil.dropCollection;

public class DistinctTest {


    private MongoCollection collection;
    private String wallStreetAvenue;

    @Before
    public void setUp() throws Exception {
        collection = createEmptyCollection("users");
        wallStreetAvenue = "22 Wall Street Avenue";
    }

    @After
    public void tearDown() throws Exception {
        dropCollection("users");
    }


    @Test
    public void distinctOnStringEntities() throws Exception {
        /* given */
        collection.save(new User("John", wallStreetAvenue));
        collection.save(new User("Smith", wallStreetAvenue));
        collection.save(new User("Peter", "24 Wall Street Avenue"));

        /* when */
        Iterator<String> addresses = collection.distinct("address", "", String.class);

        /* then */
        assertThat(addresses.next()).isEqualTo(wallStreetAvenue);
        assertThat(addresses.next()).isEqualTo("24 Wall Street Avenue");
        assertThat(addresses.hasNext()).isFalse();
    }

    @Test
    public void distinctOnIntegerEntities() throws Exception {
        /* given */
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        collection.save(new Poi(wallStreetAvenue, 125, 72));

        /* when */
        Iterator<Integer> addresses = collection.distinct("coordinate.lat", "", Integer.class);

        /* then */
        assertThat(addresses.next()).isEqualTo(1);
        assertThat(addresses.next()).isEqualTo(125);
        assertThat(addresses.hasNext()).isFalse();
    }

    @Test
    public void distinctOnTypedProperty() throws Exception {
        /* given */
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        collection.save(new Poi(wallStreetAvenue, 125, 72));

        /* when */
        Iterator<Coordinate> coordinates = collection.distinct("coordinate", "", Coordinate.class);

        /* then */
        Coordinate first = coordinates.next();
        assertThat(first.lat).isEqualTo(1);
        assertThat(first.lng).isEqualTo(2);
        Coordinate second = coordinates.next();
        assertThat(second.lat).isEqualTo(125);
        assertThat(second.lng).isEqualTo(72);
        assertThat(coordinates.hasNext()).isFalse();
    }

    @Test
    public void distinctWithQuery() throws Exception {
        /* given */
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        collection.save(new Poi(wallStreetAvenue, 1, 2));
        String emptyAddress = null;
        collection.save(new Poi(emptyAddress, 125, 72));

        /* when */
        Iterator<Coordinate> coordinates = collection.distinct("coordinate", "{address:{$exists:true}}", Coordinate.class);

        /* then */
        Coordinate first = coordinates.next();
        assertThat(first.lat).isEqualTo(1);
        assertThat(first.lng).isEqualTo(2);
        assertThat(coordinates.hasNext()).isFalse();
    }
}
