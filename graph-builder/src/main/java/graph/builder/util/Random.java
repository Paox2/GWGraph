package graph.builder.util;

import java.util.UUID;

public class Random {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
