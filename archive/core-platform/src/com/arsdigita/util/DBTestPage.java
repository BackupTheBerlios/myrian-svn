/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the ACS Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/acspl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import org.apache.log4j.Category;

/**
 * This page checks to make sure that the database is still working 
 * properly.  If it is, the page contains the word "success".  If it
 * is not then it returned the word "failed".
 *
 * To use this, simply map it to a URL in one of your dispatchers.
 * You can then point the correct keepalive script to point at the page
 * and look at the output
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #1 $, $Date: 2002/06/19 $
 */
public class DBTestPage extends Page {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/DBTestPage.java#1 $ by $Author: randyg $, $DateTime: 2002/06/19 16:09:25 $";

    private static Category s_log =
        Category.getInstance(DBTestPage.class);
    
    /** 
     *  This creates a new page with the correct label and print listener
     */
    public DBTestPage() {
        super();
        setTitle(new Label(new PrintListener() {
                public void prepare(PrintEvent e) {        
                    Label label = (Label)e.getTarget();
                    try {
                        DataQuery query = SessionManager.getSession()
                            .retrieveQuery("com.arsdigita.util.dbTest");
                        if (query.next()) {
                            label.setLabel("success");
                            // it executed successfully
                            query.close();
                        } else {
                            label.setLabel("failed");
                        }
                    } catch (PersistenceException exception) {
                        // it failed
                        label.setLabel("failed");
                    }
                }
            }));
        lock();
    }

    

}


  
