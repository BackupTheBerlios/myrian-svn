package com.redhat.persistence.jdo;

import java.util.List;

interface ClassInfo {
    List getAllFields(Class pcClass);
    List getAllTypes(Class pcClass);
    String numberToName(Class pcClass, int fieldNumber);
    Class numberToType(Class pcClass, int fieldNumber);
    /**
     * Returns the first occurrence of the specified field in the most derived
     * class.
     **/
    int nameToNumber(Class pcClass, String fieldName);
}
