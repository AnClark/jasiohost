/*
 *  Copyright 2009 Martin Roth (mhroth@gmail.com)
 * 
 *  This file is part of JAsioHost.
 *
 *  JAsioHost is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JAsioHost is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JAsioHost.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.synthbot.jasiohost;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AsioChannelInfo {

  private final int index;
  private final boolean isInput;
  private volatile boolean isActive;
  private final int channelGroup;
  private final AsioSampleType sampleType;
  private final String name;
  private final ByteBuffer[] nativeBuffers;
  private volatile int bufferIndex;
  
  private AsioChannelInfo(int index, boolean isInput, boolean isActive, int channelGroup, AsioSampleType sampleType, String name) {
    this.index = index;
    this.isInput = isInput;
    this.isActive = isActive;
    this.channelGroup = channelGroup;
    this.sampleType = sampleType;
    this.name = name;
    nativeBuffers = new ByteBuffer[2];
  }
  
  public int getChannelIndex() {
    return index;
  }
  
  public boolean isInput() {
    return isInput;
  }
  
  public boolean isActive() {
    return isActive;
  }
  
  public int getChannelGroup() {
    return channelGroup;
  }
  
  public AsioSampleType getSampleType() {
    return sampleType;
  }
  
  public String getChannelName() {
    return name;
  }
  
  /**
   * Returns the current buffer to read or write from, with the position reset to zero. The endian-ness
   * of the buffer and of the underlying system has been accounted for. Note that input buffers 
   * <code>isInput()</code> are read-only.
   */
  public ByteBuffer getByteBuffer() {
    return nativeBuffers[bufferIndex];
  }

  protected void setBufferIndex(int bufferIndex) {
    this.bufferIndex = bufferIndex;
    nativeBuffers[bufferIndex].rewind(); // reset position to start of buffer
  }
  
  protected void setByteBuffers(ByteBuffer buffer0, ByteBuffer buffer1) {
    if (buffer0 == null || buffer1 == null) {
      // the ByteBuffer references are cleared
      isActive = false;
      nativeBuffers[0] = null;
      nativeBuffers[1] = null;
    } else {
      nativeBuffers[0] = isInput ? buffer0.asReadOnlyBuffer() : buffer0;
      nativeBuffers[1] = isInput ? buffer1.asReadOnlyBuffer() : buffer1;
      if (sampleType.name().contains("MSB")) {
        nativeBuffers[0].order(ByteOrder.BIG_ENDIAN); // set the endian-ness of the buffers
        nativeBuffers[1].order(ByteOrder.BIG_ENDIAN); // according to the sample type      
      } else {
        nativeBuffers[0].order(ByteOrder.LITTLE_ENDIAN);
        nativeBuffers[1].order(ByteOrder.LITTLE_ENDIAN);
      }
      isActive = true;
    }
  }

  /*
   * equals() is overridden such that it may be used in a Set
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AsioChannelInfo)) {
      return false;
    } else {
      AsioChannelInfo channelInfo = (AsioChannelInfo) o;
      return (channelInfo.getChannelIndex() == index && channelInfo.isInput() == isInput);
    }
  }
  
  /*
   * hashCode() overridden in order to accompany equals()
   */
  @Override
  public int hashCode() {
    return isInput ? index : ~index + 1; // : 2's complement
  }
  
  /**
   * Returns a string description of the channel in the format, 
   * "Output Channel 0: Analog Out 1/2 Delta-AP [1], ASIOSTInt32LSB, group 0, inactive"
   */
  @Override
  public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(isInput ? "Input" : "Output");
	sb.append(" Channel "); sb.append(Integer.toString(index));
	sb.append(": "); sb.append(name);
	sb.append(", "); sb.append(sampleType.toString());
	sb.append(", group "); sb.append(Integer.toString(channelGroup));
	sb.append(", "); sb.append(isActive ? "active" : "inactive");
	return sb.toString();
  }  
}
