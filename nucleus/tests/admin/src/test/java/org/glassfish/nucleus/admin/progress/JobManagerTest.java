/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.nucleus.admin.progress;

import java.io.File;
import static org.glassfish.tests.utils.NucleusTestUtils.*;
import  org.glassfish.tests.utils.NucleusTestUtils;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * This tests the functionality of JobManager, list-jobs
 * @author Bhakti Mehta
 */
@Test(testName="JobManagerTest", enabled=true)
public class JobManagerTest {
    private File nucleusRoot  = NucleusTestUtils.getNucleusRoot();
    
    private final String COMMAND1 = "progress-simple";

    @BeforeTest
    public void setUp() throws Exception {
        nadmin("stop-domain");
        //delete jobs.xml incase there were other jobs run
        deleteJobsFile();

        nadmin("start-domain");


    }

    @AfterTest
    public void cleanUp() throws Exception {
        nadmin("stop-domain");
        nadmin("start-domain");

    }
    
    @Test(enabled=true)
    public void noJobsTest() {
        nadmin("stop-domain");
        //delete jobs.xml incase there were other jobs run
        deleteJobsFile();
        nadmin("start-domain");
        String result = null;
        result = nadminWithOutput("list-jobs").outAndErr;
        assertTrue(matchString("Nothing to list", result));

     
    }
    
    @Test(dependsOnMethods = { "noJobsTest" },enabled=true)
    public void runJobTest() {
        String result = null;

        //run a command
        assertTrue(COMMAND1, nadmin(COMMAND1));
        //check list-jobs
        result = nadminWithOutput("list-jobs").out;
        assertTrue( result.contains(COMMAND1) && result.contains("COMPLETED"));
        //check list-jobs with id 1
        result = nadminWithOutput("list-jobs","1").out;
        assertTrue( result.contains(COMMAND1) && result.contains("COMPLETED"));
        //shutdown server
        assertTrue( nadmin("stop-domain"));
        //restart
        assertTrue( nadmin("start-domain"));
        //check jobs
        result = nadminWithOutput("list-jobs","1").out;
        assertTrue( result.contains(COMMAND1) && result.contains("COMPLETED"));
        nadmin("start-domain");

    }

    @Test(dependsOnMethods = { "runJobTest" }, enabled=false)
       public void runDetachTest() {
           String result = null;
           //shutdown server
           assertTrue( nadmin("stop-domain"));

           //delete the jobs file
           deleteJobsFile();

           //restart
           assertTrue( nadmin("start-domain"));
           result = nadminDetachWithOutput( COMMAND1).out;
           assertTrue( result.contains("Job ID:"));


           //delete the jobs file
           deleteJobsFile();



       }

    /**
     * This will delete the jobs.xml file
     */
    private void deleteJobsFile() {
        File configDir = new File(nucleusRoot,"domains/domain1/config");
        File jobsFile = new File (configDir,"jobs.xml");
        System.out.println("Deleting.. " + jobsFile);
        if (jobsFile!= null && jobsFile.exists()) {
            jobsFile.delete();
        }
    }
    

}
