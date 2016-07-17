package com.jnape.palatable.lambda.lens.lenses.impure;

import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.lens.Lens;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.jnape.palatable.lambda.lens.functions.Set.set;
import static com.jnape.palatable.lambda.lens.functions.View.view;
import static com.jnape.palatable.lambda.lens.lenses.impure.ImpureMapLens.keys;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ImpureMapLensTest {

    private Map<String, Integer> m;

    @Before
    public void setUp() {
        m = new HashMap<String, Integer>() {{
            put("foo", 1);
            put("bar", 2);
            put("baz", 3);
        }};
    }

    @Test
    public void atKeyFocusesOnValueAtKey() {
        Lens<Map<String, Integer>, Map<String, Integer>, Integer, Integer> atFoo = ImpureMapLens.atKey("foo");

        assertEquals((Integer) 1, view(atFoo, m));

        Map<String, Integer> updated = set(atFoo, -1, m);
        assertEquals(new HashMap<String, Integer>() {{
            put("foo", -1);
            put("bar", 2);
            put("baz", 3);
        }}, updated);
        assertSame(m, updated);
    }

    @Test
    public void keysFocusesOnKeys() {
        Lens<Map<String, Integer>, Map<String, Integer>, Set<String>, Set<String>> keys = keys();

        assertEquals(m.keySet(), view(keys, m));

        Map<String, Integer> updated = set(keys, new HashSet<>(asList("bar", "baz", "quux")), m);
        assertEquals(new HashMap<String, Integer>() {{
            put("bar", 2);
            put("baz", 3);
            put("quux", null);
        }}, updated);
        assertSame(m, updated);
    }

    @Test
    public void valuesFocusesOnValues() {
        Lens<Map<String, Integer>, Map<String, Integer>, Collection<Integer>, Fn2<String, Integer, Integer>> values = ImpureMapLens.values();

        assertEquals(m.values(), view(values, m));

        Map<String, Integer> updated = set(values, (k, v) -> k.length() + v, m);
        assertEquals(new HashMap<String, Integer>() {{
            put("foo", 4);
            put("bar", 5);
            put("baz", 6);
        }}, updated);
        assertSame(m, updated);
    }

    @Test
    public void invertedFocusesOnMapWithKeysAndValuesSwitched() {
        Lens.Simple<Map<String, Integer>, Map<Integer, String>> inverted = ImpureMapLens.inverted();

        assertEquals(new HashMap<Integer, String>() {{
            put(1, "foo");
            put(2, "bar");
            put(3, "baz");
        }}, view(inverted, m));

        Map<String, Integer> updated = set(inverted, new HashMap<Integer, String>() {{
            put(2, "bar");
            put(3, "baz");
        }}, m);
        assertEquals(new HashMap<String, Integer>() {{
            put("bar", 2);
            put("baz", 3);
        }}, updated);
        assertSame(m, updated);
    }
}