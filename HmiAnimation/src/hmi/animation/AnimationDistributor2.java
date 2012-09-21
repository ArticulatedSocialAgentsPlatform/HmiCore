/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.animation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class AnimationDistributor2
{

    private DatagramSocket socket;
    private int serverPort = 4445;
    private int senderPort = 4433;

    // private int listenerPort = 4433;
    private InetAddress serverAddress;
    private ByteArrayOutputStream byteOut = null;
    private DataOutputStream dataOut = null;
    private DatagramPacket packet = null;

    byte[] buf;

    SkeletonPose pose;

    private VJoint root;

    private ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>(8);
    private ArrayList<SkeletonPose> skeletonPoses = new ArrayList<SkeletonPose>(16);
    // private String id;
    private List<String> partList;

    private static final ArrayList<String> EMPTY_LIST = new ArrayList<String>(0);

    public AnimationDistributor2(int serverPort, int senderPort) throws Exception
    {
        this.serverPort = serverPort;
        this.senderPort = senderPort;
//        log.info("AnimationDistributor running...");
        init();
    }

    public AnimationDistributor2() throws Exception
    {
        this(4445, 4433);
    }

    private Skeleton testSkeleton;
    private SkeletonPose testPose;

    private void init() throws Exception
    {
        String characterId = "Relion";
        addSkeleton(relionSkeleton(characterId));

        List<String> partIds = Arrays.asList(new String[] { "Pelvis", "L5", "L3", "T12", "T8", "Neck", "Head", "LeftShoulder",
                "LeftUpperArm", "LeftForeArm", "LeftHand", "RightShoulder", "RightUpperArm", "RightForeArm", "RightHand", "LeftUpperLeg",
                "LeftLowerLeg", "LeftFoot", "LeftToe", "RightUpperLeg", "RightLowerLeg", "RightFoot", "RightToe", });

        addSkeletonPose("pose", characterId, partIds, "R");
        initSender();

    }

    /*
     * Adds a new Skeleton.
     * Avoids adding Skeletons with duplicate ids.
     * Returns true when the Skeleton has actually been added.
     * This method is protected, so it can be used for JUnit testing.
     * Normally, Skeletons will be added only by querying a "remote" site.
     */
    protected boolean addSkeleton(Skeleton newSkel)
    {
        for (Skeleton skel : skeletons)
        {
            if (skel.getId().equals(newSkel.getId())) return false;
        }
        skeletons.add(newSkel);
        return true;
    }

    /**
     * Returns a Skeleton for the specified id, if this id is actually supported.
     * If not, a null Skeleton is returned.
     */
    public Skeleton getSkeleton(String id)
    {
        for (Skeleton skel : skeletons)
        {
            if (skel.getId().equals(id)) return skel;
        }
        return null;
    }

    /**
     * Returns some Skeleton that is supported, more or less assuming there is
     * only one candidate. If there are several Skeletons available, any of
     * these might be returned.
     * If no Skeletons are available, null is returned.
     */
    public Skeleton getSkeleton()
    {
        return skeletons.isEmpty() ? null : skeletons.get(0);
    }

    /**
     * Returns a List of Skeleton ids, of all supported Skeletons
     */
    public List<String> getAvailableSkeletonIds()
    {
        ArrayList<String> result = new ArrayList<String>(skeletons.size());
        for (Skeleton skel : skeletons)
            result.add(skel.getId());
        return result;
    }

    /**
     * Allocate a new SkeletonPose for one of the Skeletons. The Skeleton must be available already,
     * and is identified by its id. The new SkeletonPose has it own id, and specified a list of parts (ids) and a config type
     * like "T1R" or "T".
     */
    public SkeletonPose addSkeletonPose(String poseId, String skeletonId, List<String> partIdents, String configType)
    {
        Skeleton skel = getSkeleton(skeletonId);
        if (skel == null)
        {
//            log.error("addSkeletonPose: Skeleton for " + skeletonId + " not available");
            return null;
        }
        SkeletonPose skPose = new SkeletonPose(poseId, skel, partIdents, configType);
        addPose(skPose);
        return skPose;
    }

    /* Add a SkeletonPose to the List */
    private void addPose(SkeletonPose pose)
    {
        skeletonPoses.add(pose);
    }

    /**
     * returns the SkeletonPose for the specified id, or null if no such
     * SkeletonPose has been defined
     */
    public SkeletonPose getSkeletonPose(String poseId)
    {
        for (SkeletonPose pose : skeletonPoses)
        {
            if (pose.getId().equals(poseId)) return pose;
        }
        return null;
    }

    /**
     * Called to take a &quot;snapshot&quot; of the current pose of all associated SkeletonPoses
     * This snaphot pose will be sent later on.
     */
    public synchronized void snapshot()
    {

        for (SkeletonPose pose : skeletonPoses)
        {
            pose.fromSkeleton();
            send(pose);
        }
    }

    /**
     * Set the UDP port for the server
     */
    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    /**
     * Set the UDP address for the server
     */
    public void setServerAddress(String host) throws UnknownHostException
    {
        serverAddress = InetAddress.getByName(host);
    }

    /* init UDP connection to server */
    private void initSender() throws UnknownHostException, SocketException
    {
        if (serverAddress == null) serverAddress = InetAddress.getLocalHost();
        socket = new DatagramSocket(senderPort);
        byteOut = new ByteArrayOutputStream();
        dataOut = new DataOutputStream(byteOut);
        // packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
    }

    private void send(SkeletonPose pose)
    {
        if (dataOut == null)
        {
//            log.error("Cannot send, no relion connection");
            return;
        }
        try
        {
            byteOut.reset();
            pose.writeBinary(dataOut);
            dataOut.flush();
            buf = byteOut.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
//            log.debug(pose.toString());
            socket.send(packet);
        }
        catch (IOException e)
        {
         //   log.error("AnimationDistributor: " + e);
        }
        finally
        {
            // socket.close();
        }
    }

    public static Skeleton relionSkeleton(String characterId)
    {
        VJoint pelvis, l5, l3, t12, t8, neck, head;
        VJoint rightShoulder, rightUpperArm, rightForeArm, rightHand;
        VJoint leftShoulder, leftUpperArm, leftForeArm, leftHand;
        VJoint rightUpperLeg, rightLowerLeg, rightFoot, rightToe;
        VJoint leftUpperLeg, leftLowerLeg, leftFoot, leftToe;

        pelvis = new VJoint(characterId + "-Pelvis", "Pelvis");
        l5 = new VJoint(characterId + "-L5", "L5");
        l3 = new VJoint(characterId + "-L3", "L3");
        t12 = new VJoint(characterId + "-T12", "T12");
        t8 = new VJoint(characterId + "-T8", "T8");
        neck = new VJoint(characterId + "-Neck", "Neck");
        head = new VJoint(characterId + "-Head", "Head");

        rightShoulder = new VJoint(characterId + "-RightShoulder", "RightShoulder");
        rightUpperArm = new VJoint(characterId + "-RightUpperArm", "RightUpperArm");
        rightForeArm = new VJoint(characterId + "-RightForeArm", "RightForeArm");
        rightHand = new VJoint(characterId + "-RightHand", "RightHand");

        leftShoulder = new VJoint(characterId + "-LeftShoulder", "LeftShoulder");
        leftUpperArm = new VJoint(characterId + "-LeftUpperArm", "LeftUpperArm");
        leftForeArm = new VJoint(characterId + "-LeftForeArm", "LeftForeArm");
        leftHand = new VJoint(characterId + "-LeftHand", "LeftHand");

        rightUpperLeg = new VJoint(characterId + "-RightUpperLeg", "RightUpperLeg");
        rightLowerLeg = new VJoint(characterId + "-RightLowerLeg", "RightLowerLeg");
        rightFoot = new VJoint(characterId + "-RightFoot", "RightFoot");
        rightToe = new VJoint(characterId + "-RightToe", "RightToe");

        leftUpperLeg = new VJoint(characterId + "-LeftUpperLeg", "LeftUpperLeg");
        leftLowerLeg = new VJoint(characterId + "-LeftLowerLeg", "LeftLowerLeg");
        leftFoot = new VJoint(characterId + "-LeftFoot", "LeftFoot");
        leftToe = new VJoint(characterId + "-LeftToe", "LeftToe");

        pelvis.addChild(l5);
        l5.addChild(l3);
        l3.addChild(t12);
        t12.addChild(t8);
        t8.addChild(neck);
        neck.addChild(head);
        t8.addChild(rightShoulder);
        rightShoulder.addChild(rightUpperArm);
        rightUpperArm.addChild(rightForeArm);
        rightForeArm.addChild(rightHand);
        t8.addChild(leftShoulder);
        leftShoulder.addChild(leftUpperArm);
        leftUpperArm.addChild(leftForeArm);
        leftForeArm.addChild(leftHand);
        pelvis.addChild(rightUpperLeg);
        rightUpperLeg.addChild(rightLowerLeg);
        rightLowerLeg.addChild(rightFoot);
        rightFoot.addChild(rightToe);
        pelvis.addChild(leftUpperLeg);
        leftUpperLeg.addChild(leftLowerLeg);
        leftLowerLeg.addChild(leftFoot);
        leftFoot.addChild(leftToe);

        // pelvis.setTranslation(22.0f, 33.0f, 44.0f);
        // rightShoulder.setTranslation(0.2f, 1.6f, 0.0f);
        // rightShoulder.setRotation(0.5f, 0.5f, 0.5f, 0.5f);

        // "Pelvis", // 1
        // "L5", // 2
        // "L3", // 3
        // "T12", // 4
        // "T8", // 5
        // "Neck", // 6
        // "Head", // 7
        // "RightShoulder", // 8
        // "RightUpperArm", // 9
        // "RightForeArm", // 10
        // "RightHand", // 11
        // "LeftShoulder", // 12
        // "LeftUpperArm", // 13
        // "LeftForeArm", // 14
        // "LeftHand", // 15
        // "RightUpperLeg", // 16
        // "RightLowerLeg", // 17
        // "RightFoot", // 18
        // "RightToe", // 19
        // "LeftUpperLeg", // 20
        // "LeftLowerLeg", // 21
        // "LeftFoot", // 22
        // "LeftToe", // 23

        Skeleton skel = new Skeleton(characterId);
        skel.addRoot(pelvis);
        return skel;
    }

}
