package com.onevizion.mylyn.connector.util;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;

public class QueryUtils {
    public enum QueryAttributes {
        CAN("can");

        @SuppressWarnings("unused")
        private String attr;

        QueryAttributes(String attr) {
            this.attr = attr;
        }
    }

    public static boolean isCan(IRepositoryQuery query) {
        return query != null && query.getAttributes().containsKey(QueryAttributes.CAN);
    }
}
