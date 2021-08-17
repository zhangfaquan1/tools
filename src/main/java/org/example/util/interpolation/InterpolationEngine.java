package org.example.util.interpolation;

public interface InterpolationEngine {

    interface Bindings {
        Object get(String name);
    }

    String combine(String template, Bindings bindings);
}
