package nirvanaExample;/*
 *
 *   Copyright (c) 1999 - 2011 my-Channels Ltd
 *   Copyright (c) 2012 - 2020 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 *   Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
 *
 */

import com.pcbsys.nirvana.client.nBaseClientException;
import com.pcbsys.nirvana.client.nChannelAlreadyExistsException;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nChannelNotFoundException;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nRequestTimedOutException;
import com.pcbsys.nirvana.client.nSecurityException;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.client.nSessionNotConnectedException;
import com.pcbsys.nirvana.client.nUnexpectedResponseException;
import com.pcbsys.nirvana.client.nUnknownRemoteRealmException;

/**
 * Creates a nirvana queue
 */
public class makeQueue extends nSampleApp {

    private static makeQueue mySelf = null;

    /**
     * This method demonstrates the Nirvana API calls necessary to create a
     * queue. It is called after all command line arguments have been received
     * and validated
     *
     * @param realmDetails a String[] containing the possible RNAME values
     * @param aqueueName the queue name to create
     */
    private void doit(String[] realmDetails, String aqueueName) {

        boolean clusterWide = false;

        // Check if the queue is to be created across a cluster
        if (System.getProperty("CLUSTERWIDE") != null) {
            try {
                Boolean b = new Boolean(System.getProperty("CLUSTERWIDE"));
                clusterWide = b.booleanValue();
            } catch (Exception ex) {
            }
        }

        mySelf.constructSession(realmDetails);

        // Creates the specified queue
        try {
            // Create a channel attributes object
            nChannelAttributes nca = createChannelAttributes(aqueueName);
            nca.setClusterWide(clusterWide);

            // Create the queue

            nQueue myQueue = mySession.createQueue(nca);

        }
        // Handle errors
        catch (nChannelNotFoundException cnfe) {
            System.out.println("The queue specified could not be found.");
            System.out.println("Please ensure that the queue exists in the REALM you connect to.");
            cnfe.printStackTrace();
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
        } catch (nUnknownRemoteRealmException urre) {
            System.out.println("The queue specified resided in a remote realm which could not be found.");
            System.out.println("Please ensure the queue name specified is correct.");
            urre.printStackTrace();
            System.exit(1);
        } catch (nRequestTimedOutException rtoe) {
            System.out.println("The requested operation has timed out waiting for a response from the REALM.");
            System.out.println("If this is a very busy REALM ask your administrator to increase the client timeout values.");
            rtoe.printStackTrace();
            System.exit(1);
        } catch (nChannelAlreadyExistsException caee) {
            System.out.println("The queue specified already exists on the REALM.");
            caee.printStackTrace();
            System.exit(1);
        } catch (nBaseClientException nbce) {
            System.out.println("An error occured while creating the Queue Attributes object.");
            nbce.printStackTrace();
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
            case 5:
                System.getProperties().put("CLUSTERWIDE", args[4]);
            case 4:
                System.getProperties().put("TYPE", args[3]);
            case 3:
                System.getProperties().put("CAPACITY", args[2]);
            case 2:
                System.getProperties().put("TTL", args[1]);
            case 1:
                if (args[0].equals("-?")) {
                    Usage();
                    UsageEnv();
                }
                System.getProperties().put("QUEUENAME", args[0]);
                break;
        }
    }

    public static void main(String[] args) {

        // Create an instance for this class
        mySelf = new makeQueue();

        // Process command line arguments
        mySelf.processArgs(args);

        // Process Environment Variables
        nSampleApp.processEnvironmentVariables();

        // Check the queue name specified
        String queueName = null;
        if (System.getProperty("QUEUENAME") != null) {
            queueName = System.getProperty("QUEUENAME");
        } else {
            Usage();
            System.exit(1);
        }


        // Process the local REALM RNAME details
        String[] rproperties = new String[4];
        rproperties = parseRealmProperties("nsp://LAPTOP-6H70OF8U:9003");

        // create the queue specified
        mySelf.doit(rproperties, queueName);
    }

    private nChannelAttributes createChannelAttributes(String aqueueName) throws nBaseClientException {
        // Create a queue attributes object
        nChannelAttributes nca = new nChannelAttributes();

        // Set the queue name
        nca.setName(aqueueName);

        // Set the queue type parameter (persistent | reliable | simple | mixed)
        if (System.getProperty("TYPE") != null) {
            if (System.getProperty("TYPE").equalsIgnoreCase("R")) {
                nca.setType(nChannelAttributes.RELIABLE_TYPE);
            } else if (System.getProperty("TYPE").equalsIgnoreCase("P")) {
                nca.setType(nChannelAttributes.PERSISTENT_TYPE);
            } else if (System.getProperty("TYPE").equalsIgnoreCase("M")) {
                nca.setType(nChannelAttributes.MIXED_TYPE);
            }
        } else {
            nca.setType(nChannelAttributes.RELIABLE_TYPE);
        }

        // Set the queue capacity parameter
        if (System.getProperty("CAPACITY") != null) {
            Integer i = new Integer(System.getProperty("CAPACITY"));
            nca.setMaxEvents(i.intValue());
        }

        // Set the queue Time to Live parameter
        if (System.getProperty("TTL") != null) {
            Integer i = new Integer(System.getProperty("TTL"));
            nca.setTTL(i.intValue());
        }
        return nca;
    }

    /**
     * Prints the usage message for this class
     */
    private static void Usage() {

        System.out.println("Usage ...\n");
        System.out.println("nmakeq <queue name> [time to live] [capacity] [type] [cluster wide] \n");

        System.out.println("<Required Arguments> \n");
        System.out.println("<queue name> - Queue name parameter for the queue to be created");
        System.out.println("\n[Optional Arguments] \n");
        System.out.println("[time to live] - The Time To Live parameter for the new queue (default: 0)");
        System.out.println("[capacity] - The Capacity parameter for the new queue (default: 0)");
        System.out.println("[type] - The type parameter for the new queue (default: S)");
        System.out.println("   R - For a reliable (stored in memory) queue with persistent eids");
        System.out.println("   P - For a persistent (stored on disk) queue");
        System.out.println("   M - For a mixed (allows both memory and persistent events) queue\n");
        System.out.println(
                "[cluster wide] - Whether the queue is cluster wide. Will only work if the realm is part of a cluster");
        System.out.println("\n\nNote: -? provides help on environment variables \n");
    }

} // End of nirvanaExample.makeQueue Class

