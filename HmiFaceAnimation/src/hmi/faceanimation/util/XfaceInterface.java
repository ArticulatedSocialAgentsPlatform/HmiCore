/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.faceanimation.util;

import hmi.faceanimation.model.MPEG4Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Provides an interface to Xface via TCP/IP. Xface accepts FAP uploads that usually contain more frames at once. In this interface, we upload one
 * frame at a time. This works, but Xface stops working when these uploads come too fast after each other. We therefore create a seperate thread so
 * the sleep does not get in the way of the GUI or whatever uses this interface.
 * </p>
 * 
 * <p>
 * We use a small state machine to keep track of our state and stops us from connecting when connected, sending when in error, etc.
 * </p>
 * 
 * @author R.C. Paul
 */
public class XfaceInterface extends Thread
{
    private MPEG4Configuration configuration;
    private Integer[] values = new Integer[68];
    private Logger logger = LoggerFactory.getLogger(XfaceInterface.class.getName());

    private enum State
    {
        START, CONNECTING, STANDBY, DIRTY, SENDING_TASK, CONNECTION_CLOSED, ERROR
    };

    private State state;
    private Socket socket;
    private PrintWriter out;
    private InputStream in;
    private int taskId = 1;
    private int ownerId;
    private int port = 50011;
    private int sleeptime = 500;
    private boolean stopThread = false;

    public XfaceInterface(int newPort)
    {
        setState(State.START);
        port = newPort;
    }

    public void connect()
    {
        setState(State.CONNECTING);
        this.start();

        try
        {
            socket = new Socket("127.0.0.1", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = socket.getInputStream();

            StringBuffer inputBuffer = new StringBuffer();
            while (true)
            {
                inputBuffer.append((char) in.read());
                if (in.available() < 1)
                    break;
            }
            // Controleren of verbinding is geaccepteerd.
            String inputLine = inputBuffer.toString();
            Pattern pattern = Pattern.compile("<notify name=\"CONNECTION_OK\" ownerId=\"([0-9]+)\" taskId=\"[0-9]+\" status=\"[0-9]+\"/>.*");
            Matcher matcher = pattern.matcher(inputLine);
            if (matcher.matches())
            {
                ownerId = Integer.parseInt(matcher.group(1));
                timedPrint("Connection made, ownerId=" + ownerId);
                setState(State.STANDBY);
            }
            else
            {
                throw new Exception("Invalid reply: " + inputLine);
            }
        }
        catch (Exception e)
        {
            setState(State.ERROR);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect()
    {
        if (out == null)
            return;
        out.close();
        try
        {
            in.close();
            socket.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        setState(State.CONNECTION_CLOSED);
        stopThread = true;
    }

    public boolean isStandby()
    {
        return (state == State.STANDBY);
    }

    /**
     * <p>
     * Sets values via a MPEG4Configuration object. This usually contains values from 0.0-1.0 for unidirectional FAPs or -1.0-1.0 for bidirectional
     * FAPs.
     * </p>
     * 
     * @param configuration
     * @see setRawValues
     */
    public void setConfiguration(MPEG4Configuration configuration)
    {
        this.configuration = configuration;
        this.values = configuration.getValues();
        setState(State.DIRTY);
    }

    /**
     * <p>
     * Sets raw values. These are directly used in the FAP uploads.
     * <p>
     * 
     * @param values
     * @see setConfiguration
     */
    public void setRawValues(Integer[] values)
    {
        this.values = values;
        setState(State.DIRTY);
    }

    private void sendConfiguration()
    {
        // convert();

        StringBuffer task = new StringBuffer();
        task.append("<task name=\"UPLOAD_FAP\" ownerId=\"" + ownerId + "\" taskId=\"");
        task.append(taskId);
        task.append("\"><param>");
        task.append("2.1 C:\\Users\\balci\\Work\\FACE\\exml2fap\\files\\TempFile 25 1\n");

        for (int i = 0; i < values.length; i++)
        {
            if (i == 0 || i == 1 || values[i] == null)
                task.append(0);
            else
                task.append(1);
            if (i < values.length)
                task.append(' ');
        }

        task.append("\n0 "); // Frame number
        for (int i = 0; i < values.length; i++)
        {
            if (i == 0 || i == 1 || values[i] == null)
                continue;
            task.append(values[i]);
            task.append(' ');
        }

        // System.out.println(task.toString());

        task.append("</param></task>");
        sendTask(taskId++, "UPLOAD_FAP", task.toString());
    }

    private void displayConfiguration()
    {
        StringBuffer task = new StringBuffer();
        task.append("<task name=\"STOP_PLAYBACK\" ownerId=\"" + ownerId + "\" taskId=\"");
        task.append(taskId);
        task.append("\" status=\"0\"></task>");
        sendTask(taskId++, "STOP_PLAYBACK", task.toString());
    }

    private void sendTask(int taskId, String task, String xml)
    {
        if (out == null)
            return;
        setState(State.SENDING_TASK);
        out.write(xml);
        out.flush();
        try
        {
            in.skip(in.available());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        setState(State.STANDBY);
    }

    private void setState(State state)
    {
        this.state = state;
    }

    private void timedPrint(String message)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        DateFormat df = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        DecimalFormat mdf = new DecimalFormat("000");
        String milliseconds = mdf.format(cal.get(Calendar.MILLISECOND));
        String time = df.format(cal.getTime()) + "." + milliseconds;
        logger.debug("[ {} ] {}", time, message);
    }

    public static void sleep(long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /*
     * This method is unused but here for future reference.
     */
    /*
     * private void convert() { if (configuration == null) return;
     * 
     * int[][] range = new int[68][]; range[2] = new int[2]; range[2][1] = 1000; range[3] = new int[2]; range[3][0] = -400; range[3][1] = 200;
     * range[4] = new int[2]; range[4][0] = -500; range[4][1] = 300; range[7] = new int[2]; range[7][0] = -500; range[7][1] = 300; range[8] = new
     * int[2]; range[8][0] = -500; range[8][1] = 300; range[9] = new int[2]; range[9][0] = -600; range[9][1] = 300; range[10] = new int[2];
     * range[10][0] = -600; range[10][1] = 300; range[5] = new int[2]; range[5][0] = -200; range[5][1] = 300; range[6] = new int[2]; range[6][0] =
     * -200; range[6][1] = 300; range[11] = new int[2]; range[11][0] = -300; range[11][1] = 300; range[12] = new int[2]; range[12][0] = -300;
     * range[12][1] = 300; range[15] = new int[2]; range[15][0] = -1500; range[15][1] = 1500; range[16] = new int[2]; range[16][0] = -500;
     * range[16][1] = 500; range[18] = new int[2]; range[18][0] = -200; range[18][1] = 1000; range[19] = new int[2]; range[19][0] = -200; range[19][1]
     * = 1000; range[20] = new int[2]; range[20][0] = -10; range[20][1] = 1000; range[21] = new int[2]; range[21][0] = -10; range[21][1] = 1000;
     * range[30] = new int[2]; range[30][0] = -50; range[30][1] = 150; range[31] = new int[2]; range[31][0] = -50; range[31][1] = 150; range[32] = new
     * int[2]; range[32][0] = -200; range[32][1] = 500; range[33] = new int[2]; range[33][0] = -200; range[33][1] = 500; range[34] = new int[2];
     * range[34][0] = -100; range[34][1] = 200; range[35] = new int[2]; range[35][0] = -100; range[35][1] = 200; range[36] = new int[2]; range[36][0]
     * = -100; range[36][1] = 200; range[37] = new int[2]; range[37][0] = -100; range[37][1] = 200; range[38] = new int[2]; range[38][0] = -200;
     * range[38][1] = 300; range[39] = new int[2]; range[39][0] = -200; range[39][1] = 300; range[40] = new int[2]; range[40][0] = 0; range[40][1] =
     * 500; range[41] = new int[2]; range[41][0] = 0; range[41][1] = 500; range[47] = new int[2]; range[47][0] = -10000; range[47][1] = 10000;
     * range[48] = new int[2]; range[48][0] = -10000; range[48][1] = 10000; range[49] = new int[2]; range[49][0] = -10000; range[49][1] = 10000;
     * range[50] = new int[2]; range[50][0] = -500; range[50][1] = 200; range[51] = new int[2]; range[51][0] = -500; range[51][1] = 200; range[52] =
     * new int[2]; range[52][0] = -200; range[52][1] = 500; range[54] = new int[2]; range[54][0] = -500; range[54][1] = 100; range[56] = new int[2];
     * range[56][0] = -1000; range[56][1] = 500; range[58] = new int[2]; range[58][0] = -100; range[58][1] = 200;
     * 
     * for (int i=0; i<68; i++) { if (range[i] != null) { int out_min, out_max, out_range; float in_min, in_max, in_range, in_pos;
     * 
     * out_min = range[i][0]; out_max = range[i][1]; out_range = out_max - out_min;
     * 
     * if (MPEG4.getFAPs().get(i).getDirectionality() == Directionality.BIDIRECTIONAL) { in_min = -1.0f; in_max = 1.0f; } else { in_min = 0.0f; in_max
     * = 1.0f; }
     * 
     * in_range = in_max - in_min; in_pos = configuration.getValue(i) * in_range + in_min; values[i] = (int) in_pos * out_range + out_min; } else {
     * values[i] = 0; } } }
     */

    public void run()
    {
        // Make sure we wait long enough after connecting.
        sleep(250);

        while (true)
        {
            sleep(sleeptime);
            if (stopThread)
                break;
            if (communicationNeeded())
                communicate();
        }
    }

    private boolean communicationNeeded()
    {
        return (state == State.DIRTY);
    }

    private void communicate()
    {
        sendConfiguration();
        sleep(sleeptime);
        displayConfiguration();
        setState(State.STANDBY);
        if (configuration != null)
            logger.debug(configuration.toString());
    }
}
