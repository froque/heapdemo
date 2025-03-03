package org.ehcache;

public record CopiedOnHeapValueHolder<T>(
        T copiedValue,
        long creationTime,
        long lastAccessTime,
        long expirationTime
) {
}
