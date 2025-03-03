package org.hibernate;

public record QueryKey(
        String sqlQueryString,
        long instanceHashCode
) {
}
