1) CLASSPATH requires: junit.jar, classes12.jar

2) make (set BUILD environment variable to specify build directory)

3) bin/run calls java with classpath aggregated from $CLASSPATH and
   build directories. it only works if called from this directory.

4) bin/run-tests uses bin/run to run tests, passed args to find of
   *Suite.java to find tests
