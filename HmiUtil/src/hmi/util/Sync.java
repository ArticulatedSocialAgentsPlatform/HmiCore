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
package hmi.util;

/**
 * Tagging interface for synchronisation in Hmi environments.
 *
 * Our Environments are generally synchronised on an implementation of this interface. For example, animation and rendering
 * are synchronised on the AnimationSync. This is to prevent the animation code from changing the state of the animation "pose"
 * in the middle of a rendering step.
 *
 * @author Dennis Reidsma
 */
//FIXME: this interface, and all syncs, should actually be in HmiEnvironmentBase, but this is not possible because HmiPhysics refers to the PhysicsSync
public interface Sync
{
}