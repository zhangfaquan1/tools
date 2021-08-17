package org.example.util.interpolation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternInterpolationEngine implements InterpolationEngine{
    private final Pattern pattern;

    public PatternInterpolationEngine(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String combine(String template, Bindings bindings) {
        StringBuffer buffer = new StringBuffer(template.length());
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String name = matcher.group(1);
            Object value = bindings.get(name);
            matcher.appendReplacement(buffer, String.valueOf(value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
