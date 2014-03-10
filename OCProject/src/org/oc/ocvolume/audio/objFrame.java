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
 * <b>description:</b> store 100ms of samples (1600 samples), and calculate the averge square mean<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> micInput<br>
 * <b>input:</b> 100ms signal<br>
 * <b>output:</b> 100ms signal, squared mean energy
 * @author Keith Fung
 */

public class objFrame{
    final int BUFFER_SIZE = 3200;
    private short sample[] = new short[(int)(BUFFER_SIZE / 2)];
    private double avgEnergy = 0;

    /**
     * constructor to calculate squared mean energy<br>
     * calls: none<br>
     * called by: volume
     * @param bufferRead 100ms speech signal
     */
    public objFrame(short bufferRead[]){

        double runningSum = 0;
        for (int c = 0; c < sample.length; c++){
            sample[c] = bufferRead[c];
            runningSum += bufferRead[c] * bufferRead[c];
        }
        avgEnergy = runningSum / (BUFFER_SIZE / 2);
    }

    /**
     * return squared mean energy<br>
     * calls: none<br>
     * called by: volume
     * @return squared mean energy
     */
    public double getAvgEnergy(){
        return avgEnergy;
    }

    /**
     * return 100ms speech signal<br>
     * calls: none<br>
     * called by: volume     
     * @return 100ms speech signal
     */
    public short[] getSample(){
        return sample;
    }
}
