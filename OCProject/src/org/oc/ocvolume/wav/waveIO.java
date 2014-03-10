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

package org.oc.ocvolume.wav;

import javax.sound.sampled.*;
import java.io.*;

/**
 * last modified: June 15, 2002<br>
 * <b>description:</b> input and output to a wave file<br>
 * <b>calls:</b> none<br>
 * <b>called:</b> tarin<br>
 * <b>input:</b> speech signal, path<br>
 * <b>output:</b> none
 * @author Keith Fung
 */
public class waveIO{
    /**
     * encoding method "pulse-code modulation"  predefined encoding
     */
    final static AudioFormat.Encoding SAMPLE_ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    /**
     * capture amound of sample per second, can be changed in the constructor
     * 16 kHz
     */
    final static float SAMPLE_RATE = 16000.0F;
    /**
     * sample size 16 bits
     */
    final static int SAMPLE_BITS = 16;
    /**
     * mono
     */
    final static int SAMPLE_CHANNELS = 1;
    /**
     * frame rate = 16000
     */
    final static float SAMPLE_FRAME_RATE = 16000.0F;
    /**
     * frame size = 2
     */
    final static int SAMPLE_FRAME_SIZE = 2;
    /**
     * use little-endian<br>
     * 00000100 00000001<br>
     * first byte 00000001<br>
     * second byte 00000100
     */
    final static boolean SAMPLE_BIG_ENDIAN = false;    
    /**
     * audio format
     */
    final static AudioFormat FORMAT = new AudioFormat(SAMPLE_ENCODING, SAMPLE_RATE,SAMPLE_BITS,SAMPLE_CHANNELS,SAMPLE_FRAME_SIZE,SAMPLE_FRAME_RATE,SAMPLE_BIG_ENDIAN);

    /**
     * write to wave file<br>
     * calls: none<br>
     * called by: train
     * @param sample the data in 16bits integer (short) array
     * @param path the save location
     */
    public static void writeWave(short sample[], String path){
        //inputting data to a wav file

        byte sampleByte[] = new byte[sample.length * 2];
        
        for (int c=0; c < sample.length; c++){
            sampleByte[2 * c] = (byte)sample[c];
            sampleByte[2 * c + 1] = (byte)(sample[c]>>8);
        }


        try {
            ByteArrayInputStream sampleByteArrayInputStream = new ByteArrayInputStream(sampleByte);
            AudioInputStream sampleAudioInputStream = new AudioInputStream (sampleByteArrayInputStream, FORMAT, sampleByte.length / SAMPLE_FRAME_SIZE);
            if (AudioSystem.write(sampleAudioInputStream,AudioFileFormat.Type.WAVE, new File(path + ".wav")) == -1){
                System.out.println("Unable to write to file");
            }
/*
            else{
                System.out.println("Finish writing to file");
            }
*/
        }
        catch (Exception e){
            e.printStackTrace();               
        }
    }

///////////////////////////////////////////////////////////////

    /**
     * loading wave file<br>
     * calls: none<br>
     * called by: train     
     * @param path of the input wave file
     * @return a short array of anysize containing the amplitudes in the wave file
     */
    public static short[] readWave(String path){


        /**
         * define a file object with the location given
         */
        File fileRead = new File(path);

        /**
         * initial buffer size
         */
        int byteRead = 16000 * 2;

        /**
         * array used to temporary store the read data
         */
        byte waveByte[] = new byte[byteRead];

        /**
         * array used to store the return data
         */
        short waveShort[];

        /**
         * store the number of bytes read from the wave file
         */
        int numByteRead;

        try {
            /**
             * Byte array that used to store data read from the wave file
             */
            ByteArrayOutputStream readByteArrayOutputStream = new ByteArrayOutputStream();

            /**
             * open the wave file
             */
            AudioInputStream readAudioInputStream = AudioSystem.getAudioInputStream(fileRead);

            while ((numByteRead = readAudioInputStream.read(waveByte, 0, waveByte.length)) != -1){
                readByteArrayOutputStream.write(waveByte,0,numByteRead);
            }

            /**
             * temporary array to store data in readByteArrayOutputStream
             */
            byte tempWaveByte[] = readByteArrayOutputStream.toByteArray();

            waveShort = new short[tempWaveByte.length / 2];

            //convert 2 bytes into a short
            for (int c = 0 ; c < waveShort.length ; c++){
                waveShort[c] = (short)((tempWaveByte[2 * c + 1] << 8) + (tempWaveByte[2 * c] >= 0 ? tempWaveByte[2 * c] : tempWaveByte[2 * c] + 256));
            }
            return waveShort;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        waveShort = new short[1];
        return waveShort;
    }
}