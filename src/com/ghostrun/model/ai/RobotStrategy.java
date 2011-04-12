/* 
 * 
 * Copyright (c) 2011 The Regents of the University of California. All
 * rights reserved.
 * 
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN
 * "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */
package com.ghostrun.model.ai;

import com.ghostrun.model.MazeGraphPoint;
import com.google.android.maps.GeoPoint;

/** Defines the strategy that a Robot (Ghost) uses to determine how
 *  to chase the player.
 *
 *  @author Ben Lickly
 */
public interface RobotStrategy {
    
    /** Calculate an adjacent point in the maze to head to,
     *  given the current point and desired definition.
     *
     *  @param location The current location in the maze graph.
     *  @param destination The desired final location.
     *  @return The adjacent maze location to move toward next. 
     */
    public MazeGraphPoint getNextWaypoint(MazeGraphPoint location,
            GeoPoint destination);
}
