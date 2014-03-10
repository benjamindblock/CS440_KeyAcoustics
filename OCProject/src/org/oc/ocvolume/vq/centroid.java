/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
* Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
* Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
*/

package org.oc.ocvolume.vq;

import java.util.Vector;
import java.util.Enumeration;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> centroid of a codebook
 * <b>calls:</b> point<br>
 * <b>called by:</b> codebook<br>
 * <b>input:</b> k-dimensional points<br>
 * <b>output:</b> distortion measure
 * @author Danny Su
 * @author Andrei Leonov
 */
public class centroid extends point{
    /**
     * distortion measure - sum of all points' distances from the centroid
     */
    protected double distortion = 0;
    /**
     * stores the points that belong to this centroid or cell
     */
    protected Vector<point> pts = new Vector<point>(0);
    /**
     * total number of points that belong to this centroid or cell
     */
    protected int total_pts;
    /**
     * constructor to create a centroid from input coordinates<br>
     * calls: none<br>
     * called by: codebook
     * @param Co coordinates array
     */
    public centroid(double Co[]){
        super(Co);
        total_pts = 0;
    }
    /**
     * get a point at specified index<br>
     * calls: none<br>
     * called by: codebook
     * @param index index number
     * @return the point at the specified index
     */
    public point getPoint(int index){
        return (point)pts.get(index);
    }
    /**
     * returns the number of points in this cell<br>
     * calls: none<br>
     * called by: codebook
     * @return number of points
     */
    public int getNumPts(){
        return total_pts;
    }
    /**
     * removes a given point from the centroid's cell<br>
     * calls: none<br>
     * called by: codebook
     * @param pt the point to be removed
     * @param dist distance from the centroid
     */
    public void remove(point pt, double dist){
        point tmpPoint = (point)pts.get(0);
        int i = -1;
        
        Enumeration<point> e = pts.elements();
        boolean found = false;
        while( e.hasMoreElements() && !found ){
            tmpPoint = (point)e.nextElement();
            i++;
            
            // find the identical point in pts vector
            if ( point.equals(pt, tmpPoint) ){
                found = true;
            }
        }
        
        if (found){
            // remove point from pts vector
            pts.remove(i);
            // update distortion measure
            distortion -= dist;
            // update number of points
            total_pts--;
        }
        else{
            System.out.println("err: point not found");
        }
    }
    /**
     * add point to centroid's cell<br>
     * calls: none<br>
     * called by: codebook
     * @param pt a point belonging to the centroid
     * @param dist distance from the centroid
     */
    public void add(point pt, double dist){
        // update number of points
        total_pts++;
        // add point to pts vector
        pts.add(pt);
        // update distortion measure
        distortion += dist;
    }
    /**
     * update centroid by taking average of all points in the cell<br>
     * calls: none<br>
     * called by: codebook
     */
    public void update(){
        double sum_coordinates[] = new double[dimension];
        point tmpPoint;
        Enumeration<point> e = pts.elements();
        
        while( e.hasMoreElements() ){
            tmpPoint = (point)e.nextElement();
            
            // calculate the sum of all coordinates
            for (int k = 0; k < dimension; k++){
                sum_coordinates[k] += tmpPoint.getCo(k);
            }
        }
        
        // divide sum of coordinates by total number points to get average
        for( int k = 0 ; k < dimension; k++){
            setCo(k, sum_coordinates[k] / (double)total_pts);
            pts = new Vector<point>(0);
        }
        
        // reset number of points
        total_pts = 0;
        // reset distortion measure
        distortion = 0;
    }
    /**
     * returns the distortion measure of the current cell<br>
     * calls: none<br>
     * called by: codebook
     * @return distortion of current cell
     */
    public double getDistortion(){
        return distortion;
    }
}