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
/**
 * Mesh deformations.
 
<p>The core-logic in this package resides in Deformer and *Deformer. Deformer
is the base class for *Deformer and provides an easy way to create other Deformers
with the same base. All examples below given for Deformer are also true for *Deformer.</p>

<p>The DeformerClient interface is to be used by classes that wishes to be a client
to the Deformer (which itself implements the DeformerServer interface). Being a
client means that changes in parameters are being passed on and that changes to
parameters can be made. This relation between server and client is currently only
1-1 and is used in cooperation with the GUI parts of the Deformer.</p>

 */
package hmi.facegraphics.deformers;