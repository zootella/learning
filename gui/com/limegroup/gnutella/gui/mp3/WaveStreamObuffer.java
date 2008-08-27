package com.limegroup.gnutella.gui.mp3;


/***************************************************************************
 *                                                                         *
 * WaveStreamObuffer.java                                                  *
 * Credit: Code modified from javazoom.jl.decoder.WaveFileObuffer          *
 * Purpose: Allows storage for recently decoded mpeg frames                *
 * Thanks to Ben Fransen, Donn Morrison.                                   *
 *                                                                         *
 ***************************************************************************/

import javazoom.jl.decoder.Obuffer;

public final class WaveStreamObuffer extends Obuffer {
    // stores decoded frame
	private final short[] buffer = new short[OBUFFERSIZE];
	private final short[] bufferp = new short[MAXCHANNELS];
	private int channels;

	public WaveStreamObuffer(int number_of_channels, int freq) {
		channels = number_of_channels;
		for(int i=0;i<channels;++i) bufferp[i] = (short)i;
	}
	
	/**
	 * append
	 *
	 * - append a value to the channel buffer
	 *
	 */
	public final void append(final int channel, final short value) {
		buffer[bufferp[channel]] = value;
		bufferp[channel] += channels;
	}
	

    /**
     * Accepts 32 new PCM samples. 
     * Stolen from superclass.
     */
	public final void appendSamples(final int channel, final float[] f)
	{
        short s;
	    for (int i=0; i<32;)
	    {
            {
                final float currFloat = f[i++];                
                s = ((currFloat > 32767.0f) ? 32767 :
                     ((currFloat < -32768.0f) ? -32768 :
                      (short) currFloat));
            }
            //			append(channel, s); 
            buffer[bufferp[channel]] = s;
            bufferp[channel] += channels;            
	    }
	}



	/**
	 * write_buffer
	 *
	 * - write a value to the buffer
	 *
	 */
	public void write_buffer(int val) {
		for(int i=0;i<channels;++i) bufferp[i] = (short)i;
	}

	/**
	 * get_data
	 *
	 * - Used by Player to retrieve the output written to the Obuffer
	 *   by the Decoder. The bytes are retrieved and sent directly to
	 *   the SourceDataLine for playing.
	 */
	public byte [] get_data() {
		byte[] theData = new byte[buffer.length*2];
		int yc = 0;
		for (int y = 0;y<buffer.length*2;y=y+2) {
			theData[y] = (byte) (buffer[yc] & 0x00FF);
			theData[y+1] =(byte) ((buffer[yc++] >>> 8) & 0x00FF);
		}
		return theData;
	}
	
	// close() code removed - not needed
	public void close() {}
	
	// clear_buffer() code removed - not needed
	public void clear_buffer() {}
	
	// set_stop_flag() code removed - not needed
	public void set_stop_flag() {}

}
