package nirvanaExample;/*
 *
 *   Copyright (c) 1999 - 2011 my-Channels Ltd
 *   Copyright (c) 2012 - 2020 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 *   Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 *
 */

import com.pcbsys.nirvana.client.nIllegalArgumentException;
import com.pcbsys.nirvana.client.nRealm;
import com.pcbsys.nirvana.client.nRealmNotFoundException;
import com.pcbsys.nirvana.client.nRequestTimedOutException;
import com.pcbsys.nirvana.client.nSecurityException;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.client.nSessionNotConnectedException;
import com.pcbsys.nirvana.client.nSessionPausedException;
import com.pcbsys.nirvana.client.nUnexpectedResponseException;


/**
 * Displays all known realm details
 */
public class getRealm extends nSampleApp {

    private static getRealm mySelf = null;

    /**
     * This method demonstrates the Nirvana API calls necessary to display a realm's details
     * It is called after all command line arguments have been received and
     * validated
     *
     * @param realmDetails a String[] containing the possible RNAME values
     * @param arealm the nRealm object to be queried
     */
    private void doit(String[] realmDetails, String arealm) {

        mySelf.constructSession(realmDetails);

        //Display the remote realm objects
        try {
            if (arealm == null || arealm.length() == 0) {
                System.out.println("Getting all known realms");
                nRealm[] arr = mySession.getRealms();

                if (arr != null) {
                    System.out.println("Returned " + arr.length + " realms");
                    for (int x = 0; x < arr.length; x++) {
                        displayRealmDetails(arr[x]);
                    }
                }
            } else {
                displayRealmDetails(mySession.getRealm(arealm));
            }
        } catch (nSessionPausedException pas) {
        } catch (nRealmNotFoundException rnfe) {
            System.out.println("The specfied realm is not known on the server.");
            System.exit(1);
        } catch (nIllegalArgumentException ex) {
        }
        //Handle errors
        catch (nSecurityException se) {
            System.out.println("Insufficient permissions for the requested operation.");
            System.out.println("Please check the ACL settings on the server.");
            se.printStackTrace();
            System.exit(1);
        } catch (nSessionNotConnectedException snce) {
            System.out.println("The session object used is not physically connected to the Nirvana realm.");
            System.out.println("Please ensure the realm is up and check your RNAME value.");
            snce.printStackTrace();
            System.exit(1);
        } catch (nUnexpectedResponseException ure) {
            System.out.println("The Nirvana REALM has returned an unexpected response.");
            System.out.println("Please ensure the Nirvana REALM and client API used are compatible.");
            ure.printStackTrace();
            System.exit(1);
        } catch (nRequestTimedOutException rtoe) {
            System.out.println("The requested operation has timed out waiting for a response from the REALM.");
            System.out.println("If this is a very busy REALM ask your administrator to increase the client timeout values.");
            rtoe.printStackTrace();
            System.exit(1);
        }
        //Close the session we opened
        try {
            nSessionFactory.close(mySession);
        } catch (Exception ex) {
        }
        //Close any other sessions within this JVM so that we can exit
        nSessionFactory.shutdown();

    }

    protected void processArgs(String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equals("-?")) {
                    Usage();
                    nSampleApp.UsageEnv();

                }
                System.getProperties().put("REALMNAME", args[0]);

                break;
        }
    }

    public static void main(String[] args) {

        //Create an instance for this class
        mySelf = new getRealm();

        //Process command line arguments
        mySelf.processArgs(args);

        //Process Environment Variables
        nSampleApp.processEnvironmentVariables();

        //Check the remote realm name specified
        String realmName = null;


        //Check the local realm details
        int idx = 0;
        String RNAME = "nsp://LAPTOP-6H70OF8U:9003";


        //Process the local REALM RNAME details
        String[] rproperties = new String[4];
        rproperties = nSampleApp.parseRealmProperties(RNAME);

        try {
            //Displays details of realm
            mySelf.doit(rproperties, realmName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method display a realm's details
     *
     * @param arealm the nRealm object to be displayed
     */
    private void displayRealmDetails(nRealm arealm) {
        System.out.println("Realm Name  = " + arealm.getName());
        System.out.println("Realm Mount = " + arealm.getMountPoint());
        String[] cons = arealm.getProtocols();
        if (cons == null) {
            System.out.println("Realm has no known protocols configured");
        } else {
            for (int x = 0; x < cons.length; x++) {
                System.out.println("Protocol " + x + ") = " + cons[x]);
            }
        }
    }


    /**
     * Prints the usage string for this class
     */
    private static void Usage() {

        System.out.println("Usage ...\n");
        System.out.println("ngetrealm [realm name]  \n");

        System.out.println("[Optional Arguments] \n");
        System.out.println(
                "[realm name] - Realm name parameter for the realm to display details for, if unspecified all known realms are displayed");

        System.out.println("\n\nNote: -? provides help on environment variables \n");
    }

} // End of nirvanaExample.getRealm Class

