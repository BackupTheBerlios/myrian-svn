/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * MetadataSuite - Suite of tests for persistence.metadata
 *
 * @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
 * @version $Revision: #2 $ $Date: 2002/05/13 $
 */
public class MetadataxSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/MetadataxSuite.java#2 $ by $Author: dennis $, $DateTime: 2002/05/13 16:12:59 $";

    public static Test suite() {
        MetadataxSuite suite = new MetadataxSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget ("com.arsdigita.persistence.Initializer");
        wrapper.setSetupSQLScript(System.getProperty("test.sql.dir") + "/persistence/aggressive-test.sql");
        wrapper.setTeardownSQLScript(System.getProperty("test.sql.dir") + "/persistence/aggressive-teardown.sql");

        return wrapper;
    }

    public static void main(String[] args) throws Exception {
      junit.textui.TestRunner.run( suite() );
        
    }

}
