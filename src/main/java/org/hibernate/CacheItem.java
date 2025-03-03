package org.hibernate;

import java.util.List;

public record CacheItem(
        long timestamp,
        List results
) {
}
