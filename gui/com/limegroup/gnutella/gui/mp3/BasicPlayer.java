package com.limegroup.gnutella.gui.mp3;

/**
 * BasicPlayer.
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.metadata.AudioMetaData;
import com.limegroup.gnutella.util.ManagedThread;

/**
 * This code was lifted from the jlGui project (below).  Made some custom
 * changes to fit into our framework.  This version of BasicPlayer can handle
 * very low bitrate (LBR, 16-74) files.
 *
 * BasicPlayer implements basics features of a player. The playback is done
 * with a thread.
 * BasicPlayer is the result of jlGui 0.5 from JavaZOOM and BaseAudioStream
 * from Matthias Pfisterer JavaSound examples.
 *
 * @author	E.B from JavaZOOM
 *
 * Homepage : http://www.javazoom.net
 *
 */
public class BasicPlayer extends AbstractAudioPlayer implements Runnable
{

    private static final Log LOG = LogFactory.getLog(BasicPlayer.class);

	private static final int		EXTERNAL_BUFFER_SIZE = 4000 * 4;

	private Thread					m_thread = null;
	private Object					m_dataSource;
	private AudioInputStream		m_audioInputStream;
	private AudioFileFormat			m_audioFileFormat;
	private SourceDataLine			m_line;
	private FloatControl			m_gainControl;
	private FloatControl			m_panControl;

	/**
	 * These variables are used to distinguish stopped, paused, playing states.
	 * We need them to control Thread.
	 */
	public static final int			PAUSED=1;
	public static final int			PLAYING=0;
	public static final int			STOPPED=2;
	public static final int			READY=3;
	private int						m_status = READY;
	private	long					doSeek = -1;
	private File					_file = null;

    // used to keep track of frames read...
    private int m_framesRead = 0;

	/**
	 * Constructs a Basic Player.
	 */
	public BasicPlayer()
	{
		m_dataSource = null;
		m_audioInputStream = null;
		m_audioFileFormat = null;
		m_line = null;
		m_gainControl = null;
		m_panControl = null;
	}

    /*****----------------------------
     * HOW TO BE A ABSTRACTAUDIOPLAYER
     *****----------------------------/

	/**
	 * Returns BasicPlayer status.
	 */
	public int getStatus() {
        switch (m_status) {
        case PAUSED:
            return STATUS_PAUSED;
        case PLAYING:
            return STATUS_PLAYING;
        case STOPPED:
            return STATUS_STOPPED;
        default:
            return STATUS_STOPPED;
        }
	}


    public void unpause() {
        resumePlayback();
    }


    public void pause() {
        pausePlayback();
    }


    public void stop() {
        stopPlayback();
    }


    public boolean play(final File toPlay) throws IOException {
        String reason;
        try {
            setDataSource(toPlay);
            return startPlayback();
        }
        catch (UnsupportedAudioFileException ignored) {
            reason = "UNSUPPORTED";
        }
        catch (LineUnavailableException ignored) {
            reason = "UNAVAILABLE";
        }
        catch (FileNotFoundException ignored) {
            reason = "MISSING";
        }
        catch (EOFException ignored) {
            reason = "CORRUPT";
        }
        catch (IOException ignored) {
            reason = "UNKNOWN";
        }
        catch (StringIndexOutOfBoundsException ignored) {
            reason = "PARSE_PROBLEM";
        }
        final String raisin = reason;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showError("PLAYLIST_CANNOT_PLAY_FILE", toPlay, "PLAYLIST_FILE_" + raisin);
            }
        });
        return false;
    }

    public int getFrameSeek() {
        return 0;
    }

 	
 	public void refresh() {
 	    if(getStatus() == STATUS_PLAYING) {
 	        fireAudioPositionUpdated(m_framesRead);
 	    }
    } 

    /*****--------------------------------
     * HOW TO BE A ABSTRACTAUDIOPLAYER END
     *****-------------------------------/


	/**
	 * Sets the data source as a file.
	 */
	private void setDataSource(File file) throws UnsupportedAudioFileException, LineUnavailableException, IOException
	{
		if (file != null)
		{
			m_dataSource = file;
			initAudioInputStream();
		}
	}


	/**
	 * Sets the data source as an url.
	 */
	private void setDataSource(URL url) throws UnsupportedAudioFileException, LineUnavailableException, IOException
	{
		if (url != null)
		{
			m_dataSource = url;
			initAudioInputStream();
		}
	}


	/**
	 * Inits Audio ressources from the data source.<br>
	 * - AudioInputStream <br>
	 * - AudioFileFormat
	 */
	private void initAudioInputStream() throws UnsupportedAudioFileException, LineUnavailableException, IOException
	{
		if (m_dataSource instanceof URL)
		{
			initAudioInputStream((URL) m_dataSource);
		}
		else if (m_dataSource instanceof File)
		{
			initAudioInputStream((File) m_dataSource);
		}
	}

	/**
	 * Inits Audio ressources from file.
	 */
	private void initAudioInputStream(File file) throws	UnsupportedAudioFileException, IOException
	{
		_file = file;
		m_audioInputStream = AudioSystem.getAudioInputStream(file);
		m_audioFileFormat = AudioSystem.getAudioFileFormat(file);
	}

	/**
	 * Inits Audio ressources from URL.
	 */
	private void initAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException
	{
		m_audioInputStream = AudioSystem.getAudioInputStream(url);
		m_audioFileFormat = AudioSystem.getAudioFileFormat(url);
	}

	/**
	 * Inits Audio ressources from AudioSystem.<br>
	 * DateSource must be present.
	 */
	protected void initLine() throws LineUnavailableException
	{
		if (m_line == null)
		{
			createLine();
			LOG.trace("Create Line OK ");
			openLine();
		}
		else
		{
			AudioFormat	lineAudioFormat = m_line.getFormat();
			AudioFormat	audioInputStreamFormat = m_audioInputStream == null ? null : m_audioInputStream.getFormat();
			if (!lineAudioFormat.equals(audioInputStreamFormat))
			{
				m_line.close();
				openLine();
			}
		}
	}

	/**
	 * Inits a DateLine.<br>
	 *
	 * We check if the line supports Volume and Pan controls.
	 *
	 * From the AudioInputStream, i.e. from the sound file, we
	 * fetch information about the format of the audio data. These
	 * information include the sampling frequency, the number of
	 * channels and the size of the samples. There information
	 * are needed to ask JavaSound for a suitable output line
	 * for this audio file.
	 * Furthermore, we have to give JavaSound a hint about how
	 * big the internal buffer for the line should be. Here,
	 * we say AudioSystem.NOT_SPECIFIED, signaling that we don't
	 * care about the exact size. JavaSound will use some default
	 * value for the buffer size.
	 */
	private void createLine() throws LineUnavailableException
	{
		if (m_line == null)
		{
			AudioFormat	sourceFormat = m_audioInputStream.getFormat();
			if(LOG.isDebugEnabled())
			    LOG.debug("Source format : " + sourceFormat);
			AudioFormat	targetFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
														sourceFormat.getSampleRate(),
														16,
														sourceFormat.getChannels(),
														sourceFormat.getChannels() * 2,
														sourceFormat.getSampleRate(),
														false);

            if(LOG.isDebugEnabled())
                LOG.debug("Target format: " + targetFormat);
			m_audioInputStream = AudioSystem.getAudioInputStream(targetFormat, m_audioInputStream);
			AudioFormat audioFormat = m_audioInputStream.getFormat();
			if(LOG.isDebugEnabled())
                LOG.debug("Create Line : " + audioFormat);
			DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
			m_line = (SourceDataLine) AudioSystem.getLine(info);

			/*-- Display supported controls --*/
			Control[] c = m_line.getControls();
			for (int p=0;p<c.length;p++)
			{
				if(LOG.isDebugEnabled())
                    LOG.debug("Controls : "+c[p].toString());
			}
			/*-- Is Gain Control supported ? --*/
			if (m_line.isControlSupported(FloatControl.Type.MASTER_GAIN))
			{
				m_gainControl = (FloatControl) m_line.getControl(FloatControl.Type.MASTER_GAIN);
				if(LOG.isDebugEnabled())
                    LOG.debug("Master Gain Control : ["+m_gainControl.getMinimum()+","+m_gainControl.getMaximum()+"],"+m_gainControl.getPrecision());
			}

			/*-- Is Pan control supported ? --*/
			if (m_line.isControlSupported(FloatControl.Type.PAN))
			{
				m_panControl = (FloatControl) m_line.getControl(FloatControl.Type.PAN);
				if(LOG.isDebugEnabled())
                    LOG.debug("Pan Control : ["+ m_panControl.getMinimum()+","+m_panControl.getMaximum()+"]," + m_panControl.getPrecision());
			}
		}
	}


	/**
	 * Opens the line.
	 */
	private void openLine() throws LineUnavailableException
	{
		if (m_line != null)
		{
			AudioFormat	audioFormat = m_audioInputStream.getFormat();
			if(LOG.isDebugEnabled())
                LOG.debug("AudioFormat : "+audioFormat);
			m_line.open(audioFormat, m_line.getBufferSize());
		}
	}

	/**
	 * Stops the playback.<br>
	 *
	 * Player Status = STOPPED.<br>
	 * Thread should free Audio ressources.
	 */
	private void stopPlayback()
	{
		if ( (m_status == PLAYING) || (m_status == PAUSED) )
		{
			if (m_line != null)
				m_line.flush();
            if (m_line != null)
				m_line.stop();
			m_status = STOPPED;
			LOG.debug("Stop called");
		}
	}

	/**
	 * Pauses the playback.<br>
	 *
	 * Player Status = PAUSED.
	 */
	private void pausePlayback()
	{
		if (m_line != null)
		{
			if (m_status == PLAYING)
			{
				m_line.flush();
				m_line.stop();
				m_status = PAUSED;
                LOG.debug("Pause called");
			}
		}
	}

	/**
	 * Resumes the playback.<br>
	 *
	 * Player Status = PLAYING.
	 */
	private void resumePlayback()
	{
		if (m_line != null)
		{
			if (m_status == PAUSED)
			{
				m_line.start();
				m_status = PLAYING;
				LOG.debug("Resume called");
			}
		}
	}

	/**
	 * Starts playback.
	 */
	private boolean startPlayback()
	{
		if ((m_status == STOPPED) || (m_status == READY))
		{
			LOG.debug("Start called");
			if (!(m_thread == null || !m_thread.isAlive()))
			{
				LOG.debug("WARNING: old thread still running!!");
				int cnt = 0;
				while (m_status != READY)
				{
					try
					{
						if (m_thread != null)
						{
							cnt++;
							m_thread.sleep(1000);
							if (cnt > 2) m_thread.interrupt();
						}
					} catch (Exception e) {
						  LOG.debug("Waiting Error", e);
					}
					if(LOG.isDebugEnabled())
					    LOG.debug("Waiting ... "+cnt);
				}
			}
			try
			{
				initLine();
			} catch (Exception e) {
                LOG.debug("cannot init Line", e);
				  //e.printStackTrace();
				  return false;
			  }
			LOG.trace("Creating new ManagedThread");
			m_thread = new ManagedThread(this, "BasicPlayer");
            m_thread.setDaemon(true);
			m_thread.start();
			if (m_line != null)
			{
				m_line.start();
				return true;
			}
		}
		return false;
	}

	/**
	 * Main loop.
	 *
	 * Player Status == STOPPED => End of Thread + Freeing Audio Ressources.<br>
	 * Player Status == PLAYING => Audio stream data sent to Audio line.<br>
	 * Player Status == PAUSED => Waiting for another status.
	 */
	public void run()
	{
		LOG.debug("Thread Running");
		//if (m_audioInputStream.markSupported()) m_audioInputStream.mark(m_audioFileFormat.getByteLength());
		//else trace(1,getClass().getName(), "Mark not supported");
		int	nBytesRead = 1;
		m_status = PLAYING;
		int nBytesCursor = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		float nFrameSize = (float) m_line.getFormat().getFrameSize();
		float nFrameRate =  m_line.getFormat().getFrameRate();
		float bytesPerSecond = nFrameSize*nFrameRate;
		int secondsTotal  =  Math.round((float)m_audioFileFormat.getByteLength()/bytesPerSecond);
		try {
		    AudioMetaData amd = AudioMetaData.parseAudioFile(_file);
		    if(amd != null)
		        secondsTotal = amd.getLength();
        } catch(IOException ignored) {}
		
        fireSeekSetupRequired(secondsTotal);
        try{
		while ( (nBytesRead != -1) && (m_status != STOPPED) )
		{
			if (m_status == PLAYING)
			{
				try
				{
					if (doSeek > -1 )
					{
						// Seek implementation. WAV format only !
						if (( getAudioFileFormat() != null) && (getAudioFileFormat().getType().toString().startsWith("WAV")) )
						{
							if ( (secondsTotal != AudioSystem.NOT_SPECIFIED) && (secondsTotal > 0) )
							{
								m_line.flush();
								m_line.stop();
								//m_audioInputStream.reset();
								m_audioInputStream.close();
								m_audioInputStream = AudioSystem.getAudioInputStream(_file);
								nBytesCursor = 0;
								if (m_audioFileFormat.getByteLength()-doSeek < abData.length) doSeek = m_audioFileFormat.getByteLength() - abData.length;
								doSeek = doSeek - doSeek%4;
								int toSkip = (int) doSeek;
								// skip(...) instead of read(...) runs out of memory ?!
								while ( (toSkip > 0) && (nBytesRead > 0) )
								{
									if (toSkip > abData.length) nBytesRead = m_audioInputStream.read(abData, 0, abData.length);
									else nBytesRead = m_audioInputStream.read(abData, 0, toSkip);
									toSkip = toSkip - nBytesRead;
									nBytesCursor = nBytesCursor + nBytesRead;
								}
								m_line.start();
							}
							else {
							    if(LOG.isDebugEnabled())
							        LOG.debug("Seek not supported for this InputStream : "+secondsTotal);
                            }
						}
						else
						{
							if(LOG.isDebugEnabled())
                                LOG.debug("Seek not supported for this InputStream : "+secondsTotal);
						}
						doSeek = -1;
					}
					nBytesRead = m_audioInputStream.read(abData, 0, abData.length);
				}
				catch (Exception e)
				{
					if(LOG.isDebugEnabled())
                        LOG.debug("InputStream error : ("+nBytesRead+")", e);
					e.printStackTrace();
					m_status = STOPPED;
				}
				if (nBytesRead >= 0)
				{
                    // make sure that you are writing an integral number of the
                    // frame size (nFrameSize).  i think this may skip a few
                    // frames but probably not a big deal.
                    if (nBytesRead % nFrameSize != 0)
                        nBytesRead -= (nBytesRead % nFrameSize);
					int	nBytesWritten = m_line.write(abData, 0, nBytesRead);
					nBytesCursor = nBytesCursor + nBytesWritten;
                    m_framesRead = 
                    ((int) Math.round((float)nBytesCursor/bytesPerSecond));
				}
			}
			else
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				    LOG.debug("can't sleep", e);
                }
			}
		}

        }finally { 
            // close the file and free the audio line.
            try{
                if (m_line != null){
                    try{
                        m_line.drain();
                        m_line.stop();
                    } finally {
                        try {
                            m_line.close();
                        }catch(SecurityException ignored){
                            LOG.trace("Cannot Free Audio ressources", ignored);
                        }
                        m_line = null;
                    }
                }
            }finally {
                if (m_audioInputStream!=null) 
                    try {
                        m_audioInputStream.close();
                    }catch(IOException ignored){}
            }
        }
		LOG.trace("Thread Stopped");
        firePlayComplete();
		m_status = READY;
	}

	/*----------------------------------------------*/
	/*--               Gain Control               --*/
	/*----------------------------------------------*/

	/**
	 * Returns true if Gain control is supported.
	 */
	public boolean hasGainControl()
	{
		return m_gainControl != null;
	}

	/**
	 * Sets Gain value.
	 * Linear scale 0.0  <-->  1.0
	 * Threshold Coef. : 1/2 to avoid saturation.
	 */
	public void setGain(double fGain)
	{
		if (hasGainControl())
		{
			double minGainDB = getMinimum();
            double ampGainDB = ((10.0f/20.0f)*getMaximum()) - getMinimum();
            double cste = Math.log(10.0)/20;
            double valueDB = minGainDB + (1/cste)*Math.log(1+(Math.exp(cste*ampGainDB)-1)*fGain);
		    //trace(1,getClass().getName(), "Gain : "+valueDB);
			m_gainControl.setValue((float)valueDB);
		}
	}

	/**
	 * Returns Gain value.
	 */
	public float getGain()
	{
		if (hasGainControl())
		{
			return m_gainControl.getValue();
		}
		else
		{
			return 0.0F;
		}
	}

	/**
	 * Gets max Gain value.
	 */
	public float getMaximum()
	{
		if (hasGainControl())
		{
			return m_gainControl.getMaximum();
		}
		else
		{
			return 0.0F;
		}
	}


	/**
	 * Gets min Gain value.
	 */
	public float getMinimum()
	{
		if (hasGainControl())
		{
			return m_gainControl.getMinimum();
		}
		else
		{
			return 0.0F;
		}
	}


	/*----------------------------------------------*/
	/*--               Pan Control                --*/
	/*----------------------------------------------*/

	/**
	 * Returns true if Pan control is supported.
	 */
	public boolean hasPanControl()
	{
		return m_panControl != null;
	}

	/**
	 * Returns Pan precision.
	 */
	public float getPrecision()
	{
		if (hasPanControl())
		{
			return m_panControl.getPrecision();
		}
		else
		{
			return 0.0F;
		}
	}


	/**
	 * Returns Pan value.
	 */
	public float getPan()
	{
		if (hasPanControl())
		{
			return m_panControl.getValue();
		}
		else
		{
			return 0.0F;
		}
	}

	/**
	 * Sets Pan value.
	 * Linear scale : -1.0 <--> +1.0
	 */
	public void setPan(float fPan)
	{
		if (hasPanControl())
		{
		   //trace(1,getClass().getName(), "Pan : "+fPan);
			m_panControl.setValue(fPan);
		}
	}


	/*----------------------------------------------*/
	/*--                   Seek                   --*/
	/*----------------------------------------------*/

	/**
	 * Sets Seek value.
	 * Linear scale : 0.0 <--> +1.0
	 */
	public void setSeek(double seek) throws IOException
	{
		double length = -1;
		if ( (m_audioFileFormat != null) && (m_audioFileFormat.getByteLength() != AudioSystem.NOT_SPECIFIED) ) length = (double) m_audioFileFormat.getByteLength();
		long newPos = (long) Math.round(seek*length);
		doSeek = newPos;
	}

	/*----------------------------------------------*/
	/*--               Audio Format               --*/
	/*----------------------------------------------*/

	/**
	 * Returns source AudioFormat.
	 */
	public AudioFormat getAudioFormat()
	{
		if (m_audioFileFormat != null)
		{
			return m_audioFileFormat.getFormat();
		}
		else return null;
	}

	/**
	 * Returns source AudioFileFormat.
	 */
	public AudioFileFormat getAudioFileFormat()
	{
		if (m_audioFileFormat != null)
		{
			return m_audioFileFormat;
		}
		else return null;
	}

	/**
	 * Gets an InputStream from File.
	 */
	protected InputStream openInput(File file) throws IOException
	{
		InputStream fileIn = new FileInputStream(file);
		BufferedInputStream bufIn = new BufferedInputStream(fileIn);
		return bufIn;
	}

	/*----------------------------------------------*/
	/*--                   Misc                   --*/
	/*----------------------------------------------*/
}
