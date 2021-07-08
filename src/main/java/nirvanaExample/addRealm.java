package nirvanaExample;
/*
 *
 *   Copyright (c) 1999 - 2011 my-Channels Ltd
 *   Copyright (c) 2012 - 2020 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 *   Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 *
 */
import com.pcbsys.nirvana.client.nIllegalArgumentException;
import com.pcbsys.nirvana.client.nNameSpaceConflictException;
import com.pcbsys.nirvana.client.nRealm;
import com.pcbsys.nirvana.client.nRealmAlreadyBoundException;
import com.pcbsys.nirvana.client.nRealmNotFoundException;
import com.pcbsys.nirvana.client.nRealmUnreachableException;
import com.pcbsys.nirvana.client.nRequestTimedOutException;
import com.pcbsys.nirvana.client.nSecurityException;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.client.nSessionNotConnectedException;
import com.pcbsys.nirvana.client.nSessionPausedException;
import com.pcbsys.nirvana.client.nUnexpectedResponseException;

/**
 * Adds a remote realm to the local one
 */
public class addRealm extends nSampleApp {

    private static addRealm mySelf = null;

    /**
     * This method demonstrates the Nirvana API calls necessary to add a realm
     * It is called after all command line arguments have been received and
     * validated
     *
     * @param realmDetails a String[] containing the possible RNAME values
     * @param arealm the  nRealm object to be added to the current realm
     */
    private void doit(String[] realmDetails, nRealm arealm) {

        mySelf.constructSession(realmDetails);

        // Add the remote realm object to the local realm. The object contains
        // all the
        System.out.println("");
        // necessary information for the 2 realms to start communicating.
        try {
            mySession.addRealm(arealm);
        } catch (nSessionPausedException pas) {
        } catch (nIllegalArgumentException ex) {
        }
        // Handle errors
        catch (nRealmNotFoundException rnfe) {
            System.out.println("The REALM specified is could not be located.");
            rnfe.printStackTrace();
            System.exit(1);
        } catch (nRealmAlreadyBoundException rabe) {
            System.out.println("The REALM specified is already bound.");
            rabe.printStackTrace();
            System.exit(1);
        } catch (nRealmUnreachableException rue) {
            System.out.println("The REALM specified by the RNAME value is not reachable.");
            System.out.println("Please ensure the realm is up and check your RNAME value.");
            rue.printStackTrace();
            System.exit(1);
        } catch (nSecurityException se) {
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
        } catch (nNameSpaceConflictException nce) {
            System.out.println("The requested operation causes a conflict with the existing namespace.");
            System.out.println("Please ensure the REALM name and details are correct.");
            nce.printStackTrace();
            System.exit(1);
        }
        // Close the session we opened
        try {
            nSessionFactory.close(mySession);
        } catch (Exception ex) {
        }
        // Close any other sessions within this JVM so that we can exit
        nSessionFactory.shutdown();
    }

    protected void processArgs(String[] args) {
        switch (args.length) {
            case 3:
                System.getProperties().put("MOUNTPOINT", args[2]);
            case 2:
                System.getProperties().put("REALMDETAILS", args[1]);
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

        // Create an instance for this class
        mySelf = new addRealm();

        // Process command line arguments
        mySelf.processArgs(args);

        // Process Environment Variables
        nSampleApp.processEnvironmentVariables();

        // Check the remote realm name specified
        String realmName = null;
        if (System.getProperty("REALMNAME") != null) {
            realmName = System.getProperty("REALMNAME");
        } else {
            Usage();
            System.exit(1);
        }

        // Check the remote realm RNAME details specified
        String REALMDETAILS = null;
        if (System.getProperty("REALMDETAILS") != null) {
            REALMDETAILS = System.getProperty("REALMDETAILS");
        } else {
            Usage();
            System.exit(1);
        }

        // Check the remote realm mountpoint details specified
        String mountPnt = null;
        mountPnt = System.getProperty("MOUNTPOINT");

        // Check the local realm details
        String RNAME = null;
        if (System.getProperty("RNAME") != null) {
            RNAME = System.getProperty("RNAME");
        } else {
            Usage();
            System.exit(1);
        }

        // Process the local REALM RNAME details
        String[] rproperties = new String[4];
        rproperties = nSampleApp.parseRealmProperties(RNAME);

        try {
            // Create an instance of the Nirvana Realm object to be added
            nRealm nr = new nRealm(realmName, nSampleApp.parseRealmProperties(REALMDETAILS));
            // Set the mountpoint in the local realm's Namespace
            if (mountPnt != null) {
                if (mountPnt.trim().length() > 0) {
                    nr.setMountPoint(mountPnt);
                }
            }
            // Add the remote realm to the local one
            mySelf.doit(rproperties, nr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prints the usage string for this class
     */
    private static void Usage() {
        System.out.println("Usage ...\n");
        System.out.println("naddrealm <realm name> <realm details> [mount point] \n");
        System.out.println("<Required Arguments> \n");
        System.out.println("<realm name> - Realm name parameter for the realm to add");
        System.out.println("<realm details> - Realm details parameter for the realm to add. Same form as RNAME");
        System.out.println("\n[Optional Arguments] \n");
        System.out.println("[mount point] - Where you would like to mount the realm within the namespace, for example /eur/uk");
        System.out.println("\n\nNote: -? provides help on environment variables \n");
    }

}

