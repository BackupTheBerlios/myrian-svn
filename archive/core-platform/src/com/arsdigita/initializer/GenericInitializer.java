/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.initializer;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public final class GenericInitializer implements Initializer {
    private final Configuration m_config = new Configuration();
    private final String m_name;

    public GenericInitializer(final String name) {
        if (name == null) throw new IllegalArgumentException();

        m_name = name;
    }

    public Configuration getConfiguration() {
        return m_config;
    }

    public void startup() throws InitializationException { /* empty */ }
    public void shutdown() throws InitializationException { /* empty */ }

    public String toString() {
        return m_name;
    }

    public boolean equals(final Object other) {
        if (other != null && other instanceof GenericInitializer) {
            return m_name.equals(((GenericInitializer) other).m_name);
        } else {
            return super.equals(other);
        }
    }

    public int hashCode() {
        return m_name.hashCode();
    }
}
