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

package org.oc.ocvolume.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

/**
 * <b>description:</b> continuous record voice from microphone, with at least 600ms pause in between words
 * or discrete recording with at least 100ms in the front and end being silent
 * default is continuous recording<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> volume<br>
 * <b>input:</b> digitalize speech signal<br>
 * <b>output:</b> speech signal stored in array
 */
public class micInput extends Thread{
    /**
     * microphone source
     */
    private TargetDataLine SampleLine;
    /**
     * encoding method "pulse-code modulation"  predefined encoding
     */
    protected final static AudioFormat.Encoding SAMPLE_ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    /**
     * capture amound of sample per second, can be changed in the constructor 16 kHz
     */
    protected final static float SAMPLE_RATE = 16000.0F;
    /**
     * sample size - 16 bits
     */
    protected final static int SAMPLE_BITS = 16;
    /**
     * sample channels
     */
    protected final static int SAMPLE_CHANNELS = 1;
    /**
     * sample frame rate
     */
    protected final static float SAMPLE_FRAME_RATE = 16000.0F;
    /**
     * sample frame size
     */
    protected final static int SAMPLE_FRAME_SIZE = 2;
    /**
     * use little-endian<br>
     * 00000100 00000001<br>
     * first byte 00000001<br>
     * second byte 00000100
     */
    protected final static boolean SAMPLE_BIG_ENDIAN = false;
    /**
     * buffersize 2 seconds
     */
    protected final static int SAMPLE_BUFFER_SIZE = (int)SAMPLE_RATE * 2;
    /**
     * audio format
     */
    protected final static AudioFormat format = new AudioFormat(SAMPLE_ENCODING, SAMPLE_RATE,SAMPLE_BITS,SAMPLE_CHANNELS,SAMPLE_FRAME_SIZE,SAMPLE_FRAME_RATE,SAMPLE_BIG_ENDIAN);
    /**
     * indicates recording status
     */
    private boolean sampleRecording = false;
    /**
     * the current word signal
     */
    private short sampleShortData[];
    /**
     * microphone source
     */
    private DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
    /**
     * buffersize in byte. 100ms
     */
    private final int BUFFER_SIZE = 3200;
    /**
     * a multiplier to determine the energy threshold
     */
    private final double AVG_ENERGY_CONST = 36;
    /**
     * if the energy is over this threshold, meaning that voice detected
     */
    private double avgEnergyThreshold = 0;
    /**
     * used to measure the envirnoment noise
     */
    private  double avgNoiseEnergy = 0;
    /**
     * read samples from targetdataline(mic)
     */
    private  byte byteBufferRead[] = new byte[BUFFER_SIZE];
    /**
     *
     */
    private  int numByteRead = 0;
    /**
     * store the converted byte to short
     */
    private  short bufferRead[] = new short[BUFFER_SIZE / 2];
    /**
     *
     */
    private  int numRead = 0;
    /**
     * 100ms frame
     */
    private  objFrame workingFrame;
    /**
     *
     */
    private  objFrame ExtractingFrame;
    /**
     * word object
     */
    private objSound voicedSample;
    /**
     *
     */
    private objSound tempSampleShortData;
    /**
     * true when there is louder sound found
     */
    private boolean  haveSound = false;
    private int silentCount = 0;    //counter for next 300ms silent after sound
    
    /**
     * sample buffer
     */
    private Vector vBufferRead = new Vector(5,5);
    /**
     *
     */
    private int tempVBufferReadSize = 0;
    /**
     * word buffer
     */
    protected Vector vBufferWord = new Vector(1,1);
    /**
     * discrete recording: collects data from targetdataline
     */
    private ByteArrayOutputStream sampleOutputStream;
    /**
     * temp array for discrete recording
     */
    private byte tempByteData[];
    private short tempShortData[];
    /**
     * continuous recording or discrete recording
     */
    private boolean continuousRecording = true;
    /**
     * Constructor
     */
    public micInput(){}
    /**
     * recording from the microphone<br>
     * calls: none<br>
     * called by: volume     
     */
    public void run(){
        if (AudioSystem.isLineSupported(info)){    //check if the line format is supported by the system
            try{
                SampleLine = (TargetDataLine) AudioSystem.getLine(info);  //Obtain the line
                
                SampleLine.open(format);  //open the line
                
                SampleLine.start(); // Begin audio capture.
                
                sampleRecording = true;
                
                if (continuousRecording){
                    
                    //continuous recording
                    System.out.println("noise measuring");
                    avgNoiseEnergy = 0;
                    for (int d = 0; d < 3; d++){    //use 100ms to determine the avg noise energy
                        numByteRead =  SampleLine.read(byteBufferRead, 0, BUFFER_SIZE);  // Read the next chunk of data from the TargetDataLine.
                        
                        //converting small-endian to short (16bits number)
                        for (int c = 0; c < numByteRead / 2; c++){
                            bufferRead[c] = (short)((byteBufferRead[2 * c + 1]<< 8) + (byteBufferRead[2 * c]>=0?byteBufferRead[2 * c]:byteBufferRead[2 * c] + 256));
                            avgNoiseEnergy += bufferRead[c] * bufferRead[c];
                        }
                        workingFrame = new objFrame(bufferRead);
                        vBufferRead.addElement(workingFrame);
                    }
                    avgNoiseEnergy /= (1600 * 3);
                    avgEnergyThreshold = avgNoiseEnergy * AVG_ENERGY_CONST;
                    
                    if (avgEnergyThreshold > 1070000000){    //(65536 / 2)^2  = 1073741824
                        System.out.println("The environment is too noisy.  Please stop and try recording in a less noisy enviroment.");
                    }
                    
                    System.out.println("noise measuring completed");
                    
                    // Here, stopped is a global boolean set by another thread.
                    while (sampleRecording) {
                        
                        numByteRead =  SampleLine.read(byteBufferRead, 0, BUFFER_SIZE);  // Read the next chunk of data from the TargetDataLine.
                        
                        //converting small-endian to short (16bits number)
                        for (int c = 0; c < numByteRead / 2; c++){
                            bufferRead[c] = (short)((byteBufferRead[2 * c + 1]<< 8) + (byteBufferRead[2 * c]>=0?byteBufferRead[2 * c]:byteBufferRead[2 * c] + 256));
                        }
                        numRead = (int)(numByteRead / 2);
                        
                        workingFrame = new objFrame(bufferRead);
                        vBufferRead.addElement(workingFrame);
                        
                        //check if a word is found
                        if ((workingFrame.getAvgEnergy() > avgEnergyThreshold)){
                            haveSound = true;
                            silentCount = 0;
                        }
                        else if (haveSound){
                            //record extra silent/noise at the end of the word
                            if (silentCount <= 4){
                                silentCount++;
                            }
                            //once the recording complete, then it is transfered to the word buffer ready to be recognized
                            else{
                                voicedSample = new objSound(vBufferRead.size() * 1600);
                                
                                //copy all the 100ms frames into one unit and store as a word
                                for (Enumeration e = vBufferRead.elements() ; e.hasMoreElements() ;) {
                                    ExtractingFrame = (objFrame)e.nextElement();
                                    voicedSample.addSound(ExtractingFrame.getSample(),(int)(BUFFER_SIZE/2));
                                }
                                
                                
                                vBufferWord.addElement(voicedSample);
                                
                                //remove all data in the vector Read except the last 400ms
                                tempVBufferReadSize = vBufferRead.size() - 4;
                                for (int c = 0; c < tempVBufferReadSize; c++){
                                    vBufferRead.removeElementAt(0);
                                }
                                
                                silentCount = 0;
                                haveSound = false;
                            }
                        }
                        //limit the buffer to store 500ms of noise when no voice detected
                        else if (!haveSound && vBufferRead.size() > 5){
                            vBufferRead.removeElementAt(0);
                        }
                        
                        
                    }
                    
                    SampleLine.stop();    //stop the TargetDataLine
                    SampleLine.close(); //close the TargetDataLine
                    
                    
                }
                else{
                    //discrete recording
                    sampleOutputStream = new ByteArrayOutputStream();
                    
                    sampleRecording = true;
                    
                    // Here, stopped is a global boolean set by another thread.
                    while (sampleRecording) {
                        numByteRead =  SampleLine.read(byteBufferRead, 0, BUFFER_SIZE);  // Read the next chunk of data from the TargetDataLine.
                        sampleOutputStream.write(byteBufferRead, 0, numByteRead); // Save this chunk of data.
                    }
                    
                    SampleLine.stop();
                    
                    numByteRead = SampleLine.read(byteBufferRead, 0, BUFFER_SIZE);//SampleBufferSize);
                    sampleOutputStream.write(byteBufferRead,0, numByteRead);
                    SampleLine.stop();
                    
                    SampleLine.close(); //close the TargetDataLine
                    
                    sampleOutputStream.close();    //close the storing array
                    
                    
                    tempByteData = sampleOutputStream.toByteArray(); //convert the store data into a byte array
                    
                    //make the bits into a short integer
                    tempShortData = new short[tempByteData.length / 2];
                    for (int c = 0; c < tempShortData.length; c++){
                        tempShortData[c] = (short)((tempByteData[2 * c + 1]<< 8) + (tempByteData[2 * c]>=0?tempByteData[2 * c]:tempByteData[2 * c] + 256));
                    }
                    
                    voicedSample = new objSound(tempShortData.length);
                    voicedSample.addSound(tempShortData,tempShortData.length);
                    vBufferWord.addElement(voicedSample);
                    
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Line not Supported");
        }
    }
    /**
     * Stop the mic input<br>
     * calls: none<br>
     * called by: volume     
     */
    public void stopRecord(){
        sampleRecording = false;
    }
    
    /**
     * return how many words are in the buffer<br>
     * calls: none<br>
     * called by: volume
     * @return number of words in the buffer
     */
    public int wordBufferSize(){
        return vBufferWord.size();
    }
    /**
     * return true if a word is stored in the buffer<br>
     * calls: none<br>
     * called by: volume
     * @return true or false indicating whether a word is in the buffer
     */
    public boolean byteArrayComplete(){
        if(vBufferWord.size() > 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * read the next element in the word buffer<br>
     * calls: none<br>
     * called by: volume     
     */
    public void newWord(){
        tempSampleShortData = (objSound)vBufferWord.firstElement();
        sampleShortData = new short[tempSampleShortData.getSoundSize()];
        //get the first element in the word buffer and save it as an normal short array
        for (int c = 0; c < tempSampleShortData.getSoundSize(); c++){
            sampleShortData[c] = tempSampleShortData.getSoundAt(c);
        }
    }
    
    /**
     * remove the first element in the word buffer<br>
     * calls: none<br>
     * called by: volume     
     */
    public void removeOldWord(){
        if (vBufferWord.size() > 0){
            vBufferWord.removeElementAt(0);
        }
        
    }
    
    /**
     * set the recording method to continuous<br>
     * calls: none<br>
     * called by: volume     
     */
    public void setContinuous(){
        continuousRecording = true;
    }
    
    /**
     * set the recording method to discrete<br>
     * calls: none<br>
     * called by: volume
     */
    public void setDiscrete(){
        continuousRecording = false;
    }
    
    /**
     * return the length of the array of the short array<br>
     * calls: none<br>
     * called by: volume
     * @return length of signal
     */
    public int returnShortSize(){
        return sampleShortData.length;
    }
    
    /**
     * return the signed short with given index<br>
     * calls: none<br>
     * called by: volume
     * @param IndexNum index number
     * @return signal at given index
     */
    public short returnShort(int IndexNum){
        return sampleShortData[IndexNum];
    }
    
    /**
     * return the signed short array<br>
     * calls: none<br>
     * called by: volume
     * @return signal array
     */
    public short[] returnShortArray(){
        return sampleShortData;
    }
}