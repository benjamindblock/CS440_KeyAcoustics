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

/**
 * last modified: June 13, 2002<br>
 * <b>description:</b> combine several 100ms frames<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> micInput<br>
 * <b>input:</b> speech signal<br>
 * <b>output:</b> speech signal
 * @author Keith Fung
 */

public class objSound{

    private short sound[];
    private int curPos = 0;

    /**
     * constructor to create a new short array
     * calls: none
     * called by: volume
     * @param totalSample length of short array
     */
    public objSound(int totalSample){
        sound = new short[totalSample];
    }

    /**
     * combining samples
     * calls: none
     * called by: volume     
     * @param sample speech signal
     * @param sampleLength speech signal length
     */
    public void addSound(short sample[], int sampleLength){
        for (int c = 0; c < sampleLength; c++){
            sound[c + curPos] = sample[c];
        }
        curPos += sampleLength;
    }
    /**
     * return speech signal with a given time(index)
     * calls: none
     * called by: volume
     * @param IndexNum time/index
     * @return amplitude for a specified time
     */
    public short getSoundAt(int IndexNum){
        return sound[IndexNum];
    }
    /**
     * return the signal
     * calls: none
     * called by: volume
     * @return signal
     */
    public int getSoundSize(){
        return sound.length;
    }
    
    /**
     * return speech signal
     * calls: none
     * called by: volume     
     * @return speech signal
     */
    public short[] getSound(){
        return sound;
    }
}