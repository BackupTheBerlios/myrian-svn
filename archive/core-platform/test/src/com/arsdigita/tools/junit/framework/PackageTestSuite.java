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

package com.arsdigita.tools.junit.framework;
import junit.framework.*;
import java.lang.reflect.*;
import java.io.File;

/**
 *  PackageTestSuite
 *
 *  This class is the foundation for the test suite methodology. At each package level,
 *  an PackageTestSuite derived class is defined.  For Ant to handle TestSuites, the class
 *  must define:
 *  <code>
 *      public static Test suite();
 *  </code>
 *
 *  In the PackageTestSuite framework, this method works as in the following example:
 *
 *    public static Test suite()
 *    {
 *        PersistenceSuite suite = new PersistenceSuite();
 *        populateSuite(suite);
 *        return suite;
 *    }
 *
 *  The PackageTestSuite.populateSuite method adds all the valid test cases in the same
 *  package as the derived Suite class. Optionally, if the property test.testpath is defined,
 *  the framework will look here. test.testpath must be the fully qualified path name.
 *
 * @author Jon Orris
 * @version $Revision: #8 $ $Date: 2003/02/07 $
 */

public class PackageTestSuite extends TestSuite {
    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/tools/junit/framework/PackageTestSuite.java#8 $ by $Author: vadim $, $DateTime: 2003/02/07 10:58:15 $";

    public PackageTestSuite() {
        super();
    }

    public PackageTestSuite(String name) {
        super(name);
    }

    public PackageTestSuite(Class testClass) {
        super(testClass);
    }

    /**
     *  Overrides TestSuite.addTestSuite. This allows the class to check for wrappers
     *  and error tags.
     *
     *  If the test class has a field named FAILS, the test will not be added to the suite.
     *  FAILS can be any public static type, such as:
     *
     *  <code> public static final boolean FAILS = true; </code>
     *
     *  If the TestCase requires initialization of some external resources, the
     *  class should implement the following method:
     *
     *  <code>
     *  public static Test suite()
     *  </code>
     *
     *  This factory method can then return the TestCase wrapped in some TestDecorator
     *  that performs initialization.
     *
     *  An example would be:
     *  <code>
     *  public FooTest extends TestCase {
     *      public static Test suite() {
     *          TestSuite suite = new TestSuite(FooTest.class);
     *          TestDecorator fooSetup = new FooSetup(suite);
     *          return fooSetup;
     *      }
     *  }
     *
     *  public FooSetup extends TestSetup {
     *      // called once before any tests are run
     *      protected void setUp() {
     *          GlobalResource.initialize();
     *          SQLLoader.loadAllSQL();
     *      }
     *
     *      // called once after all tests are run
     *      protected void tearDown() {
     *          GlobalResource.cleanup();
     *          SQLLoader.clearDatabase();
     *      }
     *  }
     *  </code>
     *
     *  There is an alternative methodology, which may be cleaner. Since this whole system,
     *  like the original Ant test setup, relies on class names, there may be a
     *  better way. If the test for some class Foo requires a TestSetup wrapper,
     *  the classes could be named as follows:
     *
     *      FooTestImpl.java - The TestCase based class. Was FooTest in prior example
     *      FooTest.java - The TestSetup derived class, which is created wrapping
     *          FooTestImpl.java.  Was FooSetup in above example
     *
     *  An example  would be:
     *  <code>
     *  public class FooTest extends TestSetup {
     *        public FooTest(Test test)
     *        {
     *            super(test);
     *        }
     *        public static Test suite() {
     *          return new FooTest(new TestSuite(FooTestImpl.class));
     *       }
     *  }
     *  </code>
     *  @param testClass The test class to add to the suite.
     */
    public void addTestSuite(final Class testClass) {

        if( Modifier.isAbstract(testClass.getModifiers()) ){
            return;
        }

        try {
            Field failure = testClass.getField("FAILS");
            // If the test class has a FAILS field, it is not ready to be integrated.
            return;
        }
        catch(Exception e) {
            // Ignored. There is no such Field defined on the class.
        }

        // See if the class defines a suite factory method.
        try {
            Method wrapperFactory = testClass.getMethod("suite", new Class[0]);
            try {
                Test testWrapper = (Test) wrapperFactory.invoke( null, new Object[0] );
                addTest( testWrapper );

            }
            catch(final Exception e) {
                // Something evil occured. The method is not static, public, etc.
                addTest( new TestCase("suiteFailed") {
                        public void testWrapperFailed() {
                            fail("Failed to invoke" + testClass.toString() + ".suite(). " + e.getMessage());
                        }
                    }
                         );
            }

        }
        // This class does not make a wrapper for itself.
        catch(NoSuchMethodException e) {
            //super.addTestSuite(testClass);
            addTest( new PackageTestSuite(testClass) );
        }

    }

    /**
     *  For each TestCase based class in the same package as the suite,
     *  add the TestCase to the suite.
     *
     *  @param suite The PackageTestSuite derived class.
     */
    protected static void populateSuite(PackageTestSuite suite) {
        String testCasePath = getTestCasePath(suite);

        File testFileDir = new File(testCasePath);
        String[] filenames = testFileDir.list();
        if( filenames != null && filenames.length > 0) {
            addTestCases(filenames, suite);
        } else {
            System.err.println("Warning: No tests found for test path: " + testCasePath);
        }
        if (suite.countTestCases() == 0) {
            System.err.println("Warning: no tests added for test path: " + testCasePath);
        }

    }

    /**
     *  Adds a given test to the suite. If the test somehow cannot be found, a
     *  failing test shall be added to the suite.
     *
     *  @param fullClassName The fully qualified name of the class.
     *      I.e. com.arsdigita.whatever.SomethingTest
     *
     *  @param suite The PackageTestSuite to add TestCases to.
     *
     */
    private static void addTestCase(final String fullClassName, PackageTestSuite suite) {
        try {
            Class theClass = Class.forName(fullClassName);
            suite.addTestSuite(theClass);
        }
        catch(final ClassNotFoundException e) {
            suite.addTest( new TestCase("testClassFailure") {
                    public void testClassFailure() {
                        fail("Unexpected failure to find test class " + fullClassName + ". " + e.getMessage());
                    }
                }
                           );
        }

    }

    /**
     *  Adds all of the valid Test classes to the suite. A valid test class is
     *  assumed to be named SomethingTest.
     *
     *  @param filenames The list of all files in the test class directory.
     *  @param suite The PackageTestSuite to add TestCases to.
     *
     */
    private static void addTestCases(String[] filenames, PackageTestSuite suite) {
        final String packageName =  getPackageName(suite);
        for( int i = 0; i < filenames.length; i++) {
            final String filename = filenames[i];

            final boolean isTestClass;

            String testClass = System.getProperty("junit.test", "");
            String testCactus = System.getProperty("junit.usecactus", "");

            if ( ! testClass.equals("") ) {
                isTestClass = filename.equals(testClass);
            } else {
                if ( testCactus.equalsIgnoreCase("true") ) {
                    isTestClass = filename.endsWith( "Test.class" );
                }
                else if ( testCactus.equalsIgnoreCase("only") ) {
                    isTestClass = filename.endsWith( "CactusTest.class" );
                }
                else {
                    isTestClass = filename.endsWith( "Test.class" ) &&
                        !filename.endsWith( "CactusTest.class" );
                }
            }

            if ( isTestClass ) {

                final String className = packageName + "." +
                    filename.substring( 0, filename.indexOf('.'));
                System.out.println("Class: " + className);

                addTestCase( className, suite );
            }
        }

    }

    public static Test suite() {
        PackageTestSuite suite = new PackageTestSuite();
        populateSuite(suite);
        return suite;
    }

    /**
     *  OUT OF DATE: Implementation needs to be altered!
     *
     *  Utility method to get the full path to the test class files.
     *  This makes several assumptions, which are now invalidated
     *  by the 6/19/01 reorganization:
     *
     *      1) When ant is running recursively, its cwd is the top level
     *      directory for the project, i.e. infrastructure/persistence.
     *
     *      2) The build system always places the test class files in
     * {cwd}/build/test
     *
     *  It is a real pity that java reflection doesn't have something like
     *  Package.getClasses()
     *
     *  @param suite The PackageTestSuite that tests are being added to. Is in same
     *               package as other tests.
     *
     *  @return The package name, i.e. com.arsdigita.whatever
     */
    private static String getTestCasePath(PackageTestSuite suite) {
        final String definedPath = System.getProperty("test.testpath");
        if( null != definedPath ) {
            return definedPath;
        }

        File current = new File("");
        final String packageName =  getPackageName(suite);
        final String pathName = current.getAbsolutePath() +
            File.separator + "build" + File.separator + "tests" +
            File.separator + packageName.replace('.', File.separatorChar);

        return pathName;
    }

    /**
     *  Utility method to get the package name from the suite, and strip the
     *  annoying leading package from 'package com.whatever'
     *
     *  @return The package name, i.e. com.arsdigita.whatever
     */
    private static String getPackageName(PackageTestSuite suite) {
        Package p = suite.getClass().getPackage();
        String fullPackageName = p.toString();
        String packageName = fullPackageName.substring(fullPackageName.indexOf(' ') + 1 );

        return packageName;
    }

}
