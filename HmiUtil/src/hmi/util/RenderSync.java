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
package hmi.util;

/**
 * Singleton for synchronization shutdown / disposal of a rendering system with the active render threads. 
 * The rendering system should not be disposed while it is in the middle of rendering one frame, since some renderers
 * may crash on that.
 *
 * @author Dennis Reidsma
 */
public final class RenderSync implements Sync
{
    private RenderSync(){}
    private static volatile Object sync = null;
    public static synchronized Object getSync()
    {
        if(sync==null)
        {
            sync = new Object();
        }
        return sync;
    }

}