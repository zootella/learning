package com.limegroup.gnutella.gui.mp3;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.ErrorService;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIConstants;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MediaButton;
import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.gui.playlist.PlaylistMediator;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.ProcessingQueue;

/** This class sets up JPanel with MediaPlayer on it, and takes care of 
 *  GUI MediaPlayer events.
 */
public final class MediaPlayerComponent
        implements AudioPlayerListener, RefreshListener, ThemeObserver {
            
    private static final Log LOG = LogFactory.getLog(MediaPlayerComponent.class);
            
    /**
     * The sole instance.
     */
    private static MediaPlayerComponent INSTANCE = null;
            
    /**
     * The MP3 player.
     */
    private final AudioPlayer PLAYER;
    
    /**
     * The ProgressBar for showing the name & play progress.
     */
    private /* final */ JProgressBar PROGRESS;
    private Dimension _progressBarDimension = new Dimension(110,20);
    
    /**
     * The ProcessingQueue that plays songs.
     *
     * A processingQueue is used so that we don't keep the thread
     * around when we're not handling songs.
     */
    private static final ProcessingQueue SONG_QUEUE =
        new ProcessingQueue("SongProcessor");
        
    /**
     * The currently playing song.
     */
    private volatile File myCurrentPlayingFile = null;
    
    /**
     * The next song that we want to play.
     */
    private volatile File nextSongToPlay = null;
    
    /**
     * Whether or not the user pressed 'stop' last.
     */
    private volatile static boolean stopWasLast = false;    
    
    /**
     * Index of where to display the name in the progress bar.
     */
    private volatile int currBeginIndex = -1;
    
    /**
     * The maximum characters to show in the progress bar.
     */
    private static final int STRING_SIZE_TO_SHOW = 24;
    
    /**
     * Constant for the play button.
     */
    private static final MediaButton PLAY_BUTTON = 
        new MediaButton("MEDIA_PLAY_BUTTON_TIP",
            "play_small_up", "play_small_dn");
    
    /**
     * Constant for the pause button.
     */
    private static final MediaButton PAUSE_BUTTON = 
        new MediaButton("MEDIA_PAUSE_BUTTON_TIP",
            "pause_small_up", "pause_small_dn");
    
    /** Constant for the stop button.
     */
    private static final MediaButton STOP_BUTTON = 
        new MediaButton("MEDIA_STOP_BUTTON_TIP",
            "stop_small_up", "stop_small_dn");
    
    /** Constant for the forward button.
     */
    private static final MediaButton FORWARD_BUTTON = 
        new MediaButton("MEDIA_FORWARD_BUTTON_TIP",
            "forward_small_up", "forward_small_dn");
    
    /** Constant for the rewind button.
     */
    private static final MediaButton REWIND_BUTTON = 
        new MediaButton("MEDIA_REWIND_BUTTON_TIP",
            "rewind_small_up", "rewind_small_dn");
    
    /**
     * The lazily constructed media panel.
     */
    private JPanel myMediaPanel = null;
    
    /**
     * Variable for the name of the current file being played.
     */
    private String currentFileName;
    
    /**
     * Lock for access to the above String.
     */
    private final Object cfnLock = new Object();
    
    /**
     * Gets the sole instance.
     */
    public static MediaPlayerComponent instance() {
        INSTANCE = new MediaPlayerComponent();
        return INSTANCE;
    }
    
    /**
     * Constructs a new <tt>MediaPlayerComponent</tt>.
     */
    private MediaPlayerComponent() {
        PLAYER = new BasicPlayer();
        PLAYER.addAudioPlayerListener(this);
        
        GUIMediator.addRefreshListener(this);
        ThemeMediator.addThemeObserver(this);
    }
    
    // inherit doc comment
    public void updateTheme() {
        PLAY_BUTTON.updateTheme();
        PAUSE_BUTTON.updateTheme();
        STOP_BUTTON.updateTheme();
        FORWARD_BUTTON.updateTheme();
        REWIND_BUTTON.updateTheme();
        PROGRESS.setString(GUIMediator.getStringResource("MEDIA_PLAYER_DEFAULT_STRING"));
    }
    
    
    /** 
     * Listens for the play button being pressed.
     */
    private class PlayListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            stopWasLast = false;

            switch(PLAYER.getStatus()) {
            case AudioPlayer.STATUS_PAUSED:
                unpause();
                break;
            case AudioPlayer.STATUS_STOPPED:
                setNextSong(GUIMediator.getPlayList().getFileToPlay());
                break;
            }
        }
    }
    
    
    /** 
     * Listens for the stopped button being pressed.
     */
    private class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            stopWasLast = true;
            stopSong();
        }
    }
    
    
    /**
     * Listens for the next button being pressed.
     */
    private class NextListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (PLAYER.getStatus() != AudioPlayer.STATUS_STOPPED) {
                // must unpause first if paused.
                if (PLAYER.getStatus() == AudioPlayer.STATUS_PAUSED)
                    PLAYER.unpause();
                
                stopSong(); // will automatically go to the next song.
            }
        }
    }
    
    
    /**
     * Listens for the back button being pressed.
     */
    private class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (PLAYER.getStatus() != AudioPlayer.STATUS_STOPPED) {
                GUIMediator.getPlayList().setBackwardsMode();
                if (PLAYER.getStatus() == AudioPlayer.STATUS_PAUSED)
                    PLAYER.unpause();
                stopSong();
            }
        }
    }
    
    
    /** 
     * Listens for the pause button being pressed.
     */
    private class PauseListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            pauseSong();
        }
    }
    
    
    /**
     * Runnable that waits a little bit before playing songs, to give the
     * user time to click 'next' a bunch.
     */
    private class SongBuffer implements Runnable {
        public void run() {
            if(getNextSong() == null) {
                LOG.trace("no next song, leaving.");
                return;
            }
            
            // if something's already playing, sleep a bit until someone
            // stops it -- hopefully shouldn't take too long.
            while(PLAYER.getStatus() != AudioPlayer.STATUS_STOPPED) {
                LOG.trace("player isn't stopped, sleeping.");
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ignored) {}
            }
                
            // if we're already playing the song, nothing to do.
            if(isPlaying(getNextSong())) {
                LOG.trace("already playing requested song, leaving.");
                return;
            }
            
            // if we're already playing something, wait until it finishes...
            while(myCurrentPlayingFile != null) {
                LOG.trace("player is still completing something, sleeping...");
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ignored) {}
            }
                
            // get the latest file
            File playFile = null;
            
            // While the song the user wants is changing, 
            // sleep a bit, so we don't play the first second
            // of each.
            while(playFile != getNextSong()) {
                if(LOG.isDebugEnabled())
                    LOG.debug("new song, setting as: " + getNextSong() + 
                              " and waiting for changes...");
                playFile = getNextSong();
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ignored) {}
            }
            
            // nothing to play?
            if(playFile == null) {
                LOG.debug("song selection cancelled, leaving.");
                return;
            }
            
            if(LOG.isTraceEnabled())
                LOG.trace("starting song: " + playFile);

            myCurrentPlayingFile = playFile;
            setNextSong(null);
            try {
            	PlaylistMediator playlist = GUIMediator.getPlayList();
            	if (playlist != null) {
            		if(!PLAYER.play(playFile)) {
            			myCurrentPlayingFile = null;
            			return;
            		}
            		GUIMediator.getPlayList().playStarted();                
            	}
            } catch (IOException ioe) {
                myCurrentPlayingFile = null;
                ErrorService.error(ioe);
            }
        }
    }

    /**
     * Constructs the media panel.
     */
    private JPanel constructMediaPanel() {
        int tempWidth = 0, tempHeight = 0;        
        tempHeight += PLAY_BUTTON.getIcon().getIconHeight()   + 2;
        tempWidth  += PLAY_BUTTON.getIcon().getIconWidth()    + 2
                   +  PAUSE_BUTTON.getIcon().getIconWidth()   + 2
                   +  STOP_BUTTON.getIcon().getIconWidth()    + 2
                   +  FORWARD_BUTTON.getIcon().getIconWidth() + 2
                   +  REWIND_BUTTON.getIcon().getIconWidth()  + 2;
        
        // create sliders
        PROGRESS = new SongProgressBar();
        PROGRESS.setMaximumSize(_progressBarDimension);
        PROGRESS.setPreferredSize(_progressBarDimension);
        PROGRESS.setString(GUIMediator.getStringResource("MEDIA_PLAYER_DEFAULT_STRING"));
        
        // setup buttons
        PLAY_BUTTON.addActionListener(new PlayListener());
        PAUSE_BUTTON.addActionListener(new PauseListener());
        STOP_BUTTON.addActionListener(new StopListener());
        FORWARD_BUTTON.addActionListener(new NextListener());
        REWIND_BUTTON.addActionListener(new BackListener());
        
        // setup sliders
        updatePBValue(0);
        
        // add everything
		JPanel buttonPanel = new BoxPanel(BoxPanel.X_AXIS);
        buttonPanel.setMaximumSize(new Dimension(tempWidth, tempHeight));
        buttonPanel.setMinimumSize(new Dimension(tempWidth, tempHeight));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(REWIND_BUTTON);
        buttonPanel.add(PLAY_BUTTON);
        buttonPanel.add(PAUSE_BUTTON);
        buttonPanel.add(STOP_BUTTON);
        buttonPanel.add(FORWARD_BUTTON);
        buttonPanel.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR));
        buttonPanel.add(PROGRESS);
        if (CommonUtils.isMacOSX())
            buttonPanel.add(Box.createHorizontalStrut(16));
		buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }
    
    /**
     * Gets the media panel, constructing it if necessary.
     */
    public JPanel getMediaPanel() {
        if (myMediaPanel == null)
            myMediaPanel = constructMediaPanel();
        return myMediaPanel;
    }
    
    /**
     * Plays the file immediately, marking the player as 'playing'.
     */
    public static void playSongImmediately(File f) {
        if(INSTANCE == null || f == null)
            return;
        
        MediaPlayerComponent.stopWasLast = false;
        // only do stuff if we're not already playing it.
        if (!MediaPlayerComponent.isPlaying(f))
            INSTANCE.setNextSong(f);
    }
    
    
    /**
     * Launches the specified song.
     */
    public static void launchAudio(File toPlay) {
        if (INSTANCE == null || toPlay == null)
            return;

        // already playing this audio file?
        if (MediaPlayerComponent.isPlaying(toPlay))
            return;
        
        switch(INSTANCE.PLAYER.getStatus()) {
        case AudioPlayer.STATUS_STOPPED:
            INSTANCE.setNextSong(toPlay);
            break;
        case AudioPlayer.STATUS_PLAYING:
            if(GUIMediator.getPlayList().isSongPlaying())
                GUIMediator.getPlayList().playSongNext(toPlay);
            else
                INSTANCE.setNextSong(toPlay);
            break;
        case AudioPlayer.STATUS_PAUSED:
            INSTANCE.setNextSong(toPlay);
            break;
        }
    }
    
    /**
     * Determines if the given file is playing right now.
     */
    public static boolean isPlaying(File f) {
        return f != null && f.equals(INSTANCE.myCurrentPlayingFile);
    }    
    
    /**
     * Pauses the currently playing audio file.
     */
    private void pauseSong() {
        if (PLAYER.getStatus() == AudioPlayer.STATUS_PAUSED) {
            if (PLAYER.getFrameSeek() != 0)
                PLAYER.stop();
            else
                PLAYER.unpause();
        } else {
            PLAYER.pause();
        }
    }
    
    /**
     * Sets the next song to be played.
     */
    private void setNextSong(File f) {
        if(LOG.isDebugEnabled())
            LOG.debug("setting next song to be: " + f);
        nextSongToPlay = f;
        stopSong();
        if(f != null)
            SONG_QUEUE.add(new SongBuffer());
    }
    
    /**
     * Gets the next song to be played.
     */
    private File getNextSong() {
        return nextSongToPlay;
    }
    
    /**
     * Stops the currently playing audio file.
     */
    private void stopSong() {
        if(PLAYER.getStatus() != AudioPlayer.STATUS_STOPPED)
            PLAYER.stop();
    }
    
    /**
     * Unpauses the song, if it was paused.
     */
    private void unpause() {
        if (PLAYER.getStatus() == AudioPlayer.STATUS_PAUSED) {
            // player was paused, play from current audio file location
            if (PLAYER.getFrameSeek() == 0)
                PLAYER.unpause();
        }
    }   
    
    /**
     * Notification that a song has finished playing.
     * If play is continuous & the user didn't manually stop,
     * queues up the next song to play.
     *
     * Implements one method of BasicPlayerListener Interface.
     */
    public void playComplete() {
        if(LOG.isDebugEnabled())
            LOG.debug("play completed for: " + myCurrentPlayingFile);
        myCurrentPlayingFile = null;
        
        // complete progress bar
        updatePBValue(PROGRESS.getMaximum());
        updatePBString("");
        
        PlaylistMediator playlist = GUIMediator.getPlayList();
        if (playlist == null)
        	return;
        // inform the GUI on whether or not we're going to continue playing.
        if (stopWasLast || !playlist.isContinuous())
            playlist.playComplete(true);
        else {
            playlist.playComplete(false);
            // if we don't already have another song to play,
            // get one.
            if (getNextSong() == null)
                setNextSong(playlist.getFileToPlay());
        }
    }
    
    /**
     * Notification of the number of frames that will be played in this song.
     * Updates the progress bar with the appropriate values.
     *
     * - called before playing a audio file
     * - gets the size of the audio file in frames
     * - sets the maximum slider value
     * Implements one method of AudioPlayerListener Interface.
     *
     */
    public void setUpSeek(int lengthInFrames) {
        // set slider max to audio file length
        updatePBValue(0);
        updatePBMaximum(lengthInFrames);
        // others may need access to this badboy....
        synchronized (cfnLock) {
            currentFileName = myCurrentPlayingFile.getName();
            if (currentFileName.length() > STRING_SIZE_TO_SHOW) {
                currentFileName = currentFileName + " *** " + 
                currentFileName + " *** ";
            }
            updatePBString(currentFileName);
            currBeginIndex = -5;
        }
    }
    
    /**
     * Updates the progress bar to have the correct progress & name.
     */
    public void updateAudioPosition(int value) {
        updatePBValue(value);
        
        synchronized (cfnLock) {
            if (currentFileName == null) return;
            if (currentFileName.length() <= STRING_SIZE_TO_SHOW) return;
            Assert.that(currentFileName.length() > (STRING_SIZE_TO_SHOW * 2));
            currBeginIndex = currBeginIndex + 5;
            if ((currBeginIndex + STRING_SIZE_TO_SHOW) >=
                currentFileName.length()) {
                currBeginIndex = currBeginIndex - (currentFileName.length()/2);
            }
            updatePBString(currentFileName.substring(
                currBeginIndex, currBeginIndex + STRING_SIZE_TO_SHOW));
        }
    }
    
    /**
     * Updates the audio player.
     */
    public void refresh() {
        PLAYER.refresh();
    }

    /**
     * Updates the maximum value of the progress bar, on the Swing thread.
     */
    private void updatePBMaximum(final int update) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PROGRESS.setMaximum(update);
            }
        });
    }
    
    /**
     * Updates the displayed value of the progress bar, on the Swing thread.
     */
    private void updatePBString(final String update) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PROGRESS.setString(update);
            }
        });
    }
    
    /**
     * Updates the current progress of the progress bar, on the Swing thread.
     */
    private void updatePBValue(final int update) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    PROGRESS.setValue(update);
                } catch(ClassCastException ignored) {
                    //see: http://bugs.limewire.com:8080/bugs/searching.jsp?disp1=l&disp2=c&disp3=o&disp4=j&l=152&c=188&m=315_1102
                    //who knows why it happens, but who cares about it.
                }
            }
        });
    }    
}
