package org.example.util.interpolation;

import java.util.Map;

public class Interpolations {

    private static final InterpolationEngine indexedInterpolationEngine = new IndexedInterpolationEngine();
    private static final InterpolationEngine namedInterpolationEngine = new NamedInterpolationEngine();

    public static String indexed(String template, Object... bindings) {
        return indexedInterpolationEngine.combine(template,
                IndexedInterpolationEngine.createBindings(bindings));
    }

    public static String named(String template, Object... bindings) {
        return namedInterpolationEngine.combine(template,
                NamedInterpolationEngine.createBindings(bindings));
    }

    public static String named(String template, Map<String, ?> bindings) {
        return named(template, bindings, null);
    }

    public static String named(String template, Map<String, ?> bindings,
                               Object defaultValue) {
        return namedInterpolationEngine.combine(template, NamedInterpolationEngine
                .createBindings(bindings, defaultValue));
    }
}
