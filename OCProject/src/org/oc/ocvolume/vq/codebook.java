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

import java.io.*;
import java.sql.*;
import java.util.StringTokenizer;
import org.oc.ocvolume.database.*;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> codebook for Vector Quantization component<br>
 * <b>calls:</b> centroid, point<br>
 * <b>called by:</b> volume, train<br>
 * <b>input:</b> speech signal<br>
 * <b>output:</b> set of centroids, set of indices
 * @author Danny Su
 * @author Andrei Leonov
 */
public class codebook{
    /**
     * split factor (should be in the range of 0.01 <= SPLIT <= 0.05)
     */
    protected final double SPLIT = 0.01;
    /**
     * minimum distortion
     */
    protected final double MIN_DISTORTION = 0.1;
    /**
     * codebook size - number of codewords (codevectors)<br>
     * default is: 256
     */
    protected int codebook_size = 256;
    /**
     * centroids array
     */
    protected centroid centroids[];
    /**
     * training points
     */
    protected point pt[];
    /**
     * dimension
     */
    protected int dimension;
    /**
     * constructor to train a codebook with given training points and codebook size<br>
     * calls: none<br>
     * called by: trainCodebook
     * @param tmpPt training vectors
     * @param size codebook size
     */
    public codebook(point tmpPt[], int size){
        // update variables
        pt = tmpPt;
        codebook_size = size;
        
        // make sure there are enough training points to train the codebook
        if (pt.length >= codebook_size){
            dimension = pt[0].getDimension();
            initialize();
        }
        else{
            System.out.println("err: not enough training points");
        }
    }
    /**
     * constructor to train a codebook with given training points and default codebook size (256)<br>
     * calls: none<br>
     * called by: trainCodebook
     * @param tmpPt training vectors
     */
    public codebook(point tmpPt[]){
        pt = tmpPt;
        
        // make sure there are enough training points to train the codebook
        if (pt.length >= codebook_size){
            dimension = pt[0].getDimension();
            initialize();
        }
        else{
            System.out.println("err: not enough training points");
        }
    }
    /**
     * constructor to load a codebook from a Result Set<br>
     * calls: centroid<br>
     * called by: volume
     * @param rs the Result Set from database
     * @param d dimension of the points in the codebook
     * @param cbk_size size of codebook
     */
    public codebook(ResultSet rs, int d, int cbk_size){
        codebook_size = cbk_size;
        dimension = d;
        
        centroids = new centroid[codebook_size];
        
        try{
            if (rs != null){
                int ctr = -1;
                while( rs.next() ){
                    ctr++;
                    double p[] = new double[dimension];                    
                    for (int k = 0; k < dimension; k++){
                        p[k] = rs.getDouble(k + 1);
                    }

                    centroids[ctr] = new centroid(p);
                }
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    /**
     * constructor to load a saved codebook from external file<br>
     * calls: centroid<br>
     * called by: volume
     * @param inputFile file location
     */
    public codebook(String inputFile){
        try{
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);
            
            String temp = br.readLine();
            StringTokenizer st = new StringTokenizer(temp);
            
            codebook_size = Integer.parseInt(st.nextToken());
            dimension = Integer.parseInt(st.nextToken());
            
            centroids = new centroid[codebook_size];
            int ctr = -1;
            while( (temp = br.readLine()) != null ){
                st = new StringTokenizer(temp);
                
                ctr++;
                double p[] = new double[dimension];
                for (int k = 0; k < dimension; k++){
                    p[k] = Double.parseDouble(st.nextToken());
                }
                centroids[ctr] = new centroid(p);
            }
            
            br.close();
        }
        catch(FileNotFoundException e){
            System.out.println("err: file not found\n" + e.toString());
        }
        catch(IOException e){
            System.out.println("err: io exception\n" + e.toString());
        }
        catch(Exception e){
            System.out.println("err: exception\n" + e.toString());
        }
    }
    /**
     * creates a codebook using LBG algorithm which includes K-means<br>
     * calls: centroid<br>
     * called by: codebook
     */
    protected void initialize(){
        double distortion_before_update = 0; // distortion measure before updating centroids
        double distortion_after_update = 0; // distortion measure after update centroids
        
        // design a 1-vector codebook
        centroids = new centroid[1];
        
        // then initialize it with (0, 0) coordinates
        double origin[] = new double[dimension];
        centroids[0] = new centroid(origin);
        
        // initially, all training points will belong to 1 single cell
        for(int i = 0; i < pt.length; i++){
            centroids[0].add(pt[i], 0);
        }
        
        // calls update to set the initial codevector as the average of all points
        centroids[0].update();
        
        // Iteration 1: repeat splitting step and K-means until required number of codewords is reached
        while( centroids.length < codebook_size ){
            // split codevectors by a binary splitting method
            split();
            
            // group training points to centroids closest to them
            groupPtoC();
            
            // Iteration 2: perform K-means algorithm
            do{
                for (int i = 0; i < centroids.length; i++){
                    distortion_before_update += centroids[i].getDistortion();
                    centroids[i].update();
                }
                
                // regroup
                groupPtoC();
                
                for (int i = 0; i < centroids.length; i++){
                    distortion_after_update += centroids[i].getDistortion();
                }
                
            }while( Math.abs(distortion_after_update - distortion_before_update) < MIN_DISTORTION );
        }
    }
    /**
     * save codebook to database<br>
     * calls: database<br>
     * called by: train
     * @param word word that the codebook is trained for
     */
    public void saveToDB(String word){
        database db = new database("codebooks");
        
        String columns[] = new String[dimension];
        String dataTypes[] = new String[dimension];
        
        for (int k = 0; k < dimension; k++){
            columns[k] = "c" + k;
            dataTypes[k] = database.DOUBLE;
        }
        
        db.createTable(word + "_cbk", columns, dataTypes);
        
        for (int c = 0; c < centroids.length; c++){
            db.insertRow(word + "_cbk", centroids[c].getAllCo());
        }
        
        db.close();
    }
    /**
     * save codebook to txt file<br>
     * calls: none<br>
     * called by: train
     * @param filepath external file path
     */
    public void saveToFile(String filepath){
        try{
            FileWriter fw = new FileWriter(filepath);
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(codebook_size + " " + dimension);
            bw.newLine();
            for (int c = 0; c < centroids.length; c++){
                //bw.write("c" + c + ": (");
                for (int k = 0; k < dimension; k++){
                    bw.write( centroids[c].getCo(k) + "" );
                    
                    if (k != dimension - 1){
                        //bw.write(", ");
                        bw.write(" ");
                    }
                }
                //bw.write(")");
                bw.newLine();
            }
            
            bw.close();
        }
        catch(FileNotFoundException e){
            System.out.println("err: file not found\n" + e.toString());
        }
        catch(IOException e){
            System.out.println("err: io exception\n" + e.toString());
        }
        catch(Exception e){
            System.out.println("err: exception\n" + e.toString());
        }
    }
    /**
     * splitting algorithm to increase number of centroids by multiple of 2<br>
     * calls: centroid<br>
     * called by: codebook
     */
    protected void split(){
        centroid temp[] = new centroid[centroids.length * 2];
        double tCo[][];
        
        for (int i = 0; i < temp.length; i += 2){
            
            tCo = new double[2][dimension];
            
            for (int j = 0; j < dimension; j++){
                tCo[0][j] = centroids[i/2].getCo(j) * (1 + SPLIT);
            }
            
            temp[i] = new centroid(tCo[0]);
            
            for (int j = 0; j < dimension; j++){
                tCo[1][j] = centroids[i/2].getCo(j) * (1 - SPLIT);
            }
            
            temp[i+1] = new centroid(tCo[1]);
        }
        
        // replace old centroids array with new one
        centroids = new centroid[temp.length];
        centroids = temp;
    }
    /**
     * quantize the input array of points in k-dimensional space<br>
     * calls: none<br>
     * called by: volume
     * @param pts points to be quantized
     * @return quantized index array
     */
    public int[] quantize(point pts[]){
        int output[] = new int[pts.length];
        
        for (int i = 0; i < pts.length; i++){
            output[i] = closestCentroidToPoint(pts[i]);
        }
        
        return output;
    }
    /**
     * calculates the distortion<br>
     * calls: none<br>
     * called by: volume
     * @param pts points to calculate the distortion with
     * @return distortion measure
     */
    public double getDistortion(point pts[]){
        double dist = 0;
        
        for (int i = 0; i < pts.length; i++){
            int index = closestCentroidToPoint(pts[i]);
            //System.out.println("got closest centroid");
            double d = getDistance(pts[i], centroids[index]);
            dist += d;
        }
        
        return dist;
    }
    /**
     * finds the closest centroid to a specific point<br>
     * calls: none<br>
     * called by: codebook
     * @param pt point
     * @return index number of the closest centroid
     */
    private int closestCentroidToPoint(point pt){
        double tmp_dist = 0;
        double lowest_dist = 0; //= getDistance(pt, centroids[0]);
        int lowest_index = 0;
        
        for (int i = 0; i < centroids.length; i++){
            tmp_dist = getDistance(pt, centroids[i]);
            if (tmp_dist < lowest_dist || i == 0){
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        
        return lowest_index;
    }
    /**
     * finds the closest centroid to a specific centroid<br>
     * calls: none<br>
     * called by: codebook
     * @param pt point
     * @return index number of the closest centroid
     */
    private int closestCentroidToCentroid(centroid c){
        double tmp_dist = 0;
        double lowest_dist = Double.MAX_VALUE;
        int lowest_index = 0;
        
        for (int i = 0; i < centroids.length; i++){
            tmp_dist = getDistance(c, centroids[i]);
            if (tmp_dist < lowest_dist && centroids[i].getNumPts() > 1){
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        
        return lowest_index;
    }
    /**
     * finds the closest point in c2's cell to c1<br>
     * calls: none<br>
     * called by: codebook
     * @param c1 first centroid
     * @param c2 second centroid
     * @return index of point
     */
    private int closestPoint(centroid c1, centroid c2){
        double tmp_dist = 0;
        double lowest_dist = getDistance(c2.getPoint(0), c1);
        int lowest_index = 0;
        
        for (int i = 1; i < c2.getNumPts(); i++){
            tmp_dist = getDistance(c2.getPoint(i), c1);
            if (tmp_dist < lowest_dist){
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        
        return lowest_index;
    }
    /**
     * grouping points to cells<br>
     * calls: none<br>
     * called by: codebook
     */
    private void groupPtoC(){
        // find closest centroid and assign point to it
        for(int i = 0; i < pt.length; i++){
            int index = closestCentroidToPoint(pt[i]);
            
            centroids[index].add(pt[i], getDistance(pt[i], centroids[index]));
        }
        
        // make sure that all centroids have at least one point assigned to it
        // no cell should be empty or else NaN error will occur due to division of 0 by 0
        for (int i = 0; i < centroids.length; i++){
            if (centroids[i].getNumPts() == 0){
                // find the closest centroid with more than one points assigned to it
                int index = closestCentroidToCentroid(centroids[i]);
                
                // find the closest point in the closest centroid's cell
                int closestIndex = closestPoint(centroids[i], centroids[index]);
                point closestPt = centroids[index].getPoint(closestIndex);
                
                centroids[index].remove(closestPt, getDistance(closestPt, centroids[index]));
                centroids[i].add(closestPt, getDistance(closestPt, centroids[i]));
            }
        }
    }
    /**
     * calculates the distance of a point to a centroid<br>
     * calls: none<br>
     * called by: codebook
     * @param tPt points
     * @param tC centroid
     */
    private double getDistance(point tPt, centroid tC){
        double distance = 0;
        double temp = 0;
        
        for (int i = 0 ; i < dimension; i++) {
            temp = tPt.getCo(i) - tC.getCo(i);
            distance += temp * temp;
            
        }
        
        distance = Math.sqrt(distance);
        
        return distance;
    }
}