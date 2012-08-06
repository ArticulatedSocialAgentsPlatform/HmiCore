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

import hmi.util.*;
import java.net.*;
import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */

public class UDPServer
{

    private DatagramSocket socket;

    private int serverport = 4445;
    private int senderport = 4433;

    SkeletonPose skelPose;
    Skeleton skel;
    private static Logger logger = LoggerFactory.getLogger(UDPServer.class.getName());

    public UDPServer()
    {
        skel = AnimationDistributor.relionSkeleton("relion");
        List<String> partIds = Arrays.asList(new String[] { "LeftShoulder", "LeftUpperArm", "LeftForeArm", "RightShoulder",
                "RightUpperArm", "RightForeArm" });

        skelPose = new SkeletonPose("pose", partIds, "R");

        try
        {
            hmi.util.Console.println("UDPServer starting on " + serverport + "...");
            socket = new DatagramSocket(serverport);
            while (true)
            {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                logger.debug("UDPServer received a packet");
                ByteArrayInputStream binIn = new ByteArrayInputStream(buf);
                DataInputStream dataIn = new DataInputStream(binIn);
                // String receivedString = dataIn.readUTF();
                logger.debug("Received:");
                for (int i = 0; i < 20; i++)
                {
                    float f = dataIn.readFloat();
                    logger.info(" " + f);
                }
                // logger.info.print();
            }

        }
        catch (IOException e)
        {
            logger.error("startServer: " + e);
        }
        finally
        {
            socket.close();
        }
    }

    public static void main(String[] arg)
    {
        hmi.util.Console.setLocation(1400, 20);
        hmi.util.Console.println("UDPServer starting...");
        new UDPServer();

    }

}
