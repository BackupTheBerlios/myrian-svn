/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
 * Any class implementing this interface may appear in an initialization
 * script read in by the Script class. A class that does this should construct
 * its own configuration object and initialize the parameters with the
 * appropriate types and default values. This object should be returned by the
 * getConfiguration() method. This configuration object will then be filled
 * out by the Script class with whatever values appear in the initialization
 * script.
 *
 *  <blockquote><pre>
 *  public class MyInitializer implements Initializer {
 *
 *      Configuration m_config = new Configuration();
 *
 *      public MyInitializer() throws InitializationException {
 *          m_config.initParameter("stringParam", "This is a usage string.",
 *                                 String.class,"This is a string.");
 *          m_config.initParameter("intParam",
 *                                 "Please enter a value for the intParam.")
 *          m_config.initParameter("listParam", "Should be a list.",
 *                                 java.util.List.class, new ArrayList());
 *      }
 *
 *      public Configuration getConfiguration() {
 *          return m_config;
 *      }
 *
 *      public void startup() {
 *          // Run startup code here.
 *      }
 *
 *      public void shutdown() {
 *          // Run shutdown code here.
 *      }
 *
 *  }
 *  </pre></blockquote>
 *
 * The following syntax may then be used in an initialization script:
 *
 *  <blockquote><pre>
 *  init MyInitializer {
 *      stringParam = "foo";
 *      intParam = 3;
 *      listParam = { "foo", "bar", "baz" };
 *  }
 *  </pre></blockquote>
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public interface Initializer {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/initializer/Initializer.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    /**
     * Returns the configuration object used by this initializer.
     **/

    Configuration getConfiguration();

    /**
     * Called on startup.
     **/

    void startup() throws InitializationException;

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/

    void shutdown()throws InitializationException;

}
