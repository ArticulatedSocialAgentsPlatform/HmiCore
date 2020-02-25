/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
/* @author Job Zwiers  
 
 */

package hmi.graphics.opengl.renderobjects;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.util.ClockListener;
import hmi.util.InputState;

import java.awt.Component;
import java.awt.event.KeyEvent;

import javax.media.opengl.glu.GLU;

/**
 * A simple form of keyboard based navigation module.
 * Keys: A and D move left and right, W and S move back and forth,
 * Left and right &quot;arrow&quot; keys rotate, Up and down &quot;arrow&quot;
 * keys move back and forth. PageUp and PageDown move up and down.
 */
public class GLNavigation2 implements GLRenderObject, ClockListener
{

    private InputState iptrack;
    private InputState.Pattern moveForward, moveBackward, fastForward, fastBackward;
    private InputState.Pattern strafeLeft, strafeRight, fastStrafeLeft, fastStrafeRight;
    private InputState.Pattern rotateLeft, rotateRight, fastRotateLeft, fastRotateRight;
    private InputState.Pattern rotateUp, rotateDown, fastRotateUp, fastRotateDown;
    private InputState.Pattern moveUp, moveDown, fastMoveUp, fastMoveDown;
    private InputState.Pattern control0, control1, control2, control3, control4, control5, control6, control7, control8, control9;
    // private InputState.Pattern objectForward, lightBackward, lightUp, lightDown, lightLeft, lightRight;
    private InputState.Pattern objectControl;
    private InputState.Pattern printControl;
    int curObjectIndex = 0;

    // private float[] position = new float[3];
    // private float[] orientation = new float[3];
    //
    private volatile boolean enabled = true;
    private double currentTime;
    private float linVel; // linear horizontal velocity
    private float vertVel; // vertical velocity
    private float strafeVel; // strafing velocity
    private float angVelx; // angular velocity, around X-axis
    private float angVely; // angular velocity, around Y-axis
    private float angVelz; // angular velocity, around Z-axis

    public static final double degToRad = 0.017453292519943296;

    float[] position = new float[3]; // position and orientation of the camera viewpoint
    float[] orientation = new float[3];

    private static final float conv = (float) (Math.PI / 180.0);
    private float[] rotation = new float[4];
    private float[] translation = new float[3];
    private float[] nullvec = new float[] { 0.0f, 0.0f, 0.0f };
    // private float scale = 1.0f;
    private float[] transformMatrix = new float[16];
    private float[] rotX = new float[16];
    private float[] rotY = new float[16];
    private float[] rotZ = new float[16];
    private float[] translationMatrix = new float[16];
    private float[] deltaPos = new float[3];

    private boolean printing = false;

    private GLU glu;

    // private SimpleLight[] light;
    // private float[] lightPosition;
    private static final int MAX_NUM_LIGHTS = 8;
    private float[][] objectPosition = new float[MAX_NUM_LIGHTS][];// lightPosition[i] is a float[3] array with the current position of light[i]

    private SimpleLight[] lights = new SimpleLight[MAX_NUM_LIGHTS];

    /**
     * set the position vector
     */
    public void setPosition(float[] pos)
    {
        position[0] = pos[0];
        position[1] = pos[1];
        position[2] = pos[2];
    }

    /**
     * set the position vector
     */
    public void setPosition(float x, float y, float z)
    {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    /**
     * set the three Euler angles
     */
    public void setOrientation(float xrot, float yrot, float zrot)
    {
        orientation[0] = xrot;
        orientation[1] = -yrot;
        orientation[2] = zrot;
    }

    public void setLinearVelocity(float linVel)
    {
        this.linVel = linVel;
    }

    public void setVerticalVelocity(float vertVel)
    {
        this.vertVel = vertVel;
    }

    public void setStrafeVelocity(float strafeVel)
    {
        this.strafeVel = strafeVel;
    }

    public void setAngularVelocityY(float angVel)
    {
        this.angVely = angVel;
    }

    /**
     * Create a new GLNavigation2 module, for controlling the viewpoint within
     * an OpenGL/Jogl scene. A GLNavigation2 is a GLRenderObject: it must be
     * &quot; rendered&quot; after static background scenery, before all other scenery.
     * A GLNavigation2 is also a ClockListener: it should receive time() callbacks
     * on a regular basis, in order to update the orientation and position.
     * It is expected that time() callbacks and glRender callbacks arrive from the same Java Thread.
     * It reacts on &quot;standrad&quot; keyboard navigation keys:
     * W/S/A/D, cursors keys, and PgUp and PgDown.
     */
    public GLNavigation2(Component c)
    {
        iptrack = new InputState(c, InputState.KEYLISTENER | InputState.MOUSELISTENER);

        int accelerator = KeyEvent.VK_SHIFT;

        objectControl = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL }, null);

        moveForward = iptrack.addPattern(new int[] { KeyEvent.VK_W }, new int[] { KeyEvent.VK_DOWN });
        moveBackward = iptrack.addPattern(new int[] { KeyEvent.VK_S }, new int[] { KeyEvent.VK_DOWN });

        fastForward = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_W }, new int[] { KeyEvent.VK_DOWN });
        fastBackward = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_S }, new int[] { KeyEvent.VK_DOWN });

        strafeLeft = iptrack.addPattern(new int[] { KeyEvent.VK_A }, new int[] { KeyEvent.VK_D });
        strafeRight = iptrack.addPattern(new int[] { KeyEvent.VK_D }, new int[] { KeyEvent.VK_A });

        fastStrafeLeft = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_A }, new int[] { KeyEvent.VK_D });
        fastStrafeRight = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_D }, new int[] { KeyEvent.VK_A });

        moveUp = iptrack.addPattern(new int[] { KeyEvent.VK_PAGE_UP }, null);
        moveDown = iptrack.addPattern(new int[] { KeyEvent.VK_PAGE_DOWN }, null);

        fastMoveUp = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_PAGE_UP }, null);
        fastMoveDown = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_PAGE_DOWN }, null);

        rotateLeft = iptrack.addPattern(new int[] { KeyEvent.VK_LEFT }, new int[] { KeyEvent.VK_RIGHT, KeyEvent.VK_ALT });
        rotateRight = iptrack.addPattern(new int[] { KeyEvent.VK_RIGHT }, new int[] { KeyEvent.VK_LEFT, KeyEvent.VK_ALT });

        fastRotateLeft = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_LEFT }, new int[] { KeyEvent.VK_RIGHT, KeyEvent.VK_ALT });
        fastRotateRight = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_RIGHT }, new int[] { KeyEvent.VK_LEFT, KeyEvent.VK_ALT });

        rotateUp = iptrack.addPattern(new int[] { KeyEvent.VK_UP }, new int[] { KeyEvent.VK_DOWN });
        rotateDown = iptrack.addPattern(new int[] { KeyEvent.VK_DOWN }, new int[] { KeyEvent.VK_UP });

        fastRotateUp = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_UP }, new int[] { KeyEvent.VK_DOWN });
        fastRotateDown = iptrack.addPattern(new int[] { accelerator, KeyEvent.VK_DOWN }, new int[] { KeyEvent.VK_UP });

        control0 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_0 }, null);
        control1 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_1 }, null);
        control2 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_2 }, null);
        control3 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_3 }, null);
        control4 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_4 }, null);
        control5 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_5 }, null);
        control6 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_6 }, null);
        control7 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_7 }, null);
        control8 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_8 }, null);
        control9 = iptrack.addPattern(new int[] { KeyEvent.VK_CONTROL, KeyEvent.VK_9 }, null);

        printControl = iptrack.addPattern(new int[] { KeyEvent.VK_P }, null);

        linVel = 2.0f;
        vertVel = 2.0f;
        strafeVel = 2.0f;

        angVelx = 20.0f;
        angVely = 40.0f;
        angVelz = 20.0f;

        Mat4f.setIdentity(rotX);
        Mat4f.setIdentity(rotY);
        Mat4f.setIdentity(rotZ);
        Mat4f.setIdentity(translationMatrix);
        Mat4f.setIdentity(transformMatrix);

    }

    /**
     * Associates some SimpleLight with this GLNavigation; the position
     * of this SimpleLight will be controlled by this GLNavigation
     */
    public synchronized void addControl(int index, float[] position)
    {
        // int index = simpleLight.getIndex();
        objectPosition[index] = position;
    }

    /**
     * Associates some SimpleLight with this GLNavigation; the position
     * of this SimpleLight will be controlled by this GLNavigation
     */
    public synchronized void addLightControl(SimpleLight simpleLight)
    {
        int index = simpleLight.getIndex();
        lights[index] = simpleLight;
        objectPosition[index] = simpleLight.getPosition();
    }

    public void setLightPosition(int light, float x, float y, float z)
    {
        lights[light].setPosition(x,y,z);
    }
    
    /**
     * Prints some info to system.out, like current position and orientation, and light positions
     */
    public void printInfo()
    {
        hmi.util.Console.println("Position: (" + translation[0] + ", " + translation[1] + ", " + translation[2] + ")");
        System.out.println("Position: (" + translation[0] + ", " + translation[1] + ", " + translation[2] + ")");
        hmi.util.Console.println("Oriention (Pitch, Yaw, Roll) : (" + orientation[0] + ", " + orientation[1] + ", " + orientation[2] + ")");
        System.out.println("Oriention (Pitch, Yaw, Roll) : (" + orientation[0] + ", " + orientation[1] + ", " + orientation[2] + ")");

        for (int i = 0; i < MAX_NUM_LIGHTS; i++)
        {
            if (lights[i] != null)
            {
                float[] lp = lights[i].getPosition();
                {
                    hmi.util.Console.println("LightPos[" + i + "] : (" + lp[0] + ", " + lp[1] + ", " + lp[2] + ")");
                    System.out.println("LightPos[" + i + "] : (" + lp[0] + ", " + lp[1] + ", " + lp[2] + ")");
                }
            }
        }
        hmi.util.Console.println("active object: " + curObjectIndex);

    }

    /**
     * Enables or disables navigation.
     */
    public void setEnabled(boolean isEnabled)
    {
        enabled = isEnabled;
    }

    public void initTime(double t)
    {
        currentTime = t;
    }

    /**
     * Callback function that must be called regularly, in essence
     * every time a new frame is rendered.
     * It updates the current position and orientation.
     */
    public synchronized void time(double t)
    {

        float linearVelocity = 0.0f;
        float vertVelocity = 0.0f;
        float strafeVelocity = 0.0f;
        float angularVelocityX = 0.0f;
        float angularVelocityY = 0.0f;
        float angularVelocityZ = 0.0f;

        float delta, angle; // deltaX,deltaZ;

        // always update time (even when temporary disabled)
        double lastTime = currentTime;
        currentTime = t;
        if (!enabled) return;

        if (moveForward.isActive()) linearVelocity = linVel;
        if (moveBackward.isActive()) linearVelocity = -linVel;

        if (fastForward.isActive()) linearVelocity = 3.0f * linVel;
        if (fastBackward.isActive()) linearVelocity = -3.0f * linVel;

        if (strafeLeft.isActive()) strafeVelocity = -strafeVel;
        if (strafeRight.isActive()) strafeVelocity = strafeVel;

        if (fastStrafeLeft.isActive()) strafeVelocity = -4.0f * strafeVel;
        if (fastStrafeRight.isActive()) strafeVelocity = 4.0f * strafeVel;

        if (moveUp.isActive()) vertVelocity = vertVel;
        if (moveDown.isActive()) vertVelocity = -vertVel;

        // if (fastMoveUp.isActive()) vertVelocity = 4.0f * vertVel;
        // if (fastMoveDown.isActive()) vertVelocity = -4.0f * vertVel;

        if (rotateLeft.isActive()) angularVelocityY = angVely;
        if (rotateRight.isActive()) angularVelocityY = -angVely;

        if (fastRotateLeft.isActive()) angularVelocityY = 2.0f * angVely;
        if (fastRotateRight.isActive()) angularVelocityY = -2.0f * angVely;

        if (rotateUp.isActive()) angularVelocityX = angVelx;
        if (rotateDown.isActive()) angularVelocityX = -angVelx;

        if (fastRotateUp.isActive()) angularVelocityX = 2.0f * angVelx;
        if (fastRotateDown.isActive()) angularVelocityX = -2.0f * angVelx;

        if (control0.isActive()) curObjectIndex = 0;
        if (control1.isActive()) curObjectIndex = 1;
        if (control2.isActive()) curObjectIndex = 2;
        if (control3.isActive()) curObjectIndex = 3;
        if (control4.isActive()) curObjectIndex = 4;
        if (control5.isActive()) curObjectIndex = 5;
        if (control6.isActive()) curObjectIndex = 6;
        if (control7.isActive()) curObjectIndex = 7;
        if (control8.isActive()) curObjectIndex = 8;
        if (control9.isActive()) curObjectIndex = 9;

        if (printControl.isActive())
        {
            if (!printing)
            {
                printInfo();
                printing = true;

            }
        }
        else
        {
            printing = false;
        }

        // Adjust position and orientation.
        delta = (float) (currentTime - lastTime);
        lastTime = currentTime;
        // Console.println("udatePosition at " + currentTime + ", delta = " + delta);

        // Update Rotation angles
        angle = orientation[0] + delta * angularVelocityX;

        if (angle >= 360.0f) angle -= 360.0f;
        if (angle <= -360.0f) angle += 360.0f;
        orientation[0] = angle;

        angle = orientation[1] + delta * angularVelocityY;
        if (angle >= 360.0f) angle -= 360.0f;
        if (angle <= -360.0f) angle += 360.0f;
        orientation[1] = angle;
        double radAngle = degToRad * orientation[1];

        // Update linear position

        deltaPos[0] = -delta * (linearVelocity * (float) (Math.sin(radAngle)) - strafeVelocity * (float) (Math.cos(radAngle)));
        deltaPos[2] = -delta * (linearVelocity * (float) (Math.cos(radAngle)) + strafeVelocity * (float) (Math.sin(radAngle)));
        deltaPos[1] = delta * vertVelocity;

        if (objectControl.isActive())
        {
            if (objectPosition[curObjectIndex] != null)
            {
                objectPosition[curObjectIndex][0] += deltaPos[0];
                objectPosition[curObjectIndex][2] += deltaPos[2];
                objectPosition[curObjectIndex][1] += deltaPos[1];
            }

        }
        else
        {
            position[0] += deltaPos[0];
            position[2] += deltaPos[2];
            position[1] += deltaPos[1];
        }

        setTranslation(-position[0], -position[1], -position[2]);
        Mat4f.setTranslation(translationMatrix, translation);

        // setRollPitchYaw(-orientation[2], -orientation[0], -orientation[1]);

        Mat4f.setXRotDegrees(rotX, -orientation[0]); // inverse pitch
        Mat4f.setYRotDegrees(rotY, -orientation[1]); // inverse yaw
        Mat4f.setZRotDegrees(rotZ, -orientation[2]); // inverse roll

        // we need an inverse roll-pitch yaw matrix: (yaw o pitch o roll)-inverse =
        // roll-inverse o pitch-inverse o yaw-inverse
        // roll = z-axis, pitch = x-axis, yaw = y-axis
        // z-axis-inverse o x-axis-invesre o y-axis-inverse o translationmatrix

        Mat4f.mul(transformMatrix, rotZ, rotX);
        Mat4f.mul(transformMatrix, rotY);
        Mat4f.mul(transformMatrix, translationMatrix);

    }

    public void setRollPitchYaw(float roll, float pitch, float yaw)
    {
        Quat4f.setFromRollPitchYawDegrees(rotation, roll, pitch, yaw);
        orientation[0] = -pitch;
        orientation[1] = -yaw;
        orientation[2] = -roll;
    }

    public void setTranslation(float tx, float ty, float tz)
    {
        translation[0] = tx;
        translation[1] = ty;
        translation[2] = tz;
    }

    /**
     * Returns the current rotation, in the form of Euler angles
     * (Rotation around x, y, and Z axis
     */
    public synchronized float[] getOrientation()
    {
        return orientation;
    }

    /**
     * Returns the current position vector
     */
    public synchronized float[] getPosition()
    {
        return position;
    }

    /**
     * Initializes navigation.
     */
    public void glInit(GLRenderContext gl)
    {
        glu = new GLU();
    }

    /**
     * Jogl render phase: transform, according to current position and orientation.
     * Note that the transformation matrix to be applied is the inverse of the
     * matrix that describes the user's position and orientation.
     */
    public void glRender(GLRenderContext glc)
    {

        // hmi.util.Console.println("explaintimer", 10000, 100, "transformMatrix: " + Mat4f.explainMat4f(transformMatrix, 8, 3, 0.01f));
        glc.gl2.glMultTransposeMatrixf(transformMatrix, 0); // since OpenGL expects column major order, we use the transposed matrix

        //
        // // Simply look at the origin of the scene, from the current camera position.
        // glu.gluLookAt((double)position[0], (double)position[1], (double)position[2], // camera position
        // 0.0, 0.0, 0.0, // look at this point
        // 0.0, 1.0, 0.0); // "up" vector

    }

}
