package com.limegroup.gnutella.gui.playlist;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIConstants;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.mp3.MediaPlayerComponent;
import com.limegroup.gnutella.gui.mp3.PlayList;
import com.limegroup.gnutella.gui.tables.AbstractTableMediator;
import com.limegroup.gnutella.gui.tables.DataLine;
import com.limegroup.gnutella.gui.tables.DragManager;
import com.limegroup.gnutella.gui.tables.FileTransfer;
import com.limegroup.gnutella.gui.tables.LimeJTable;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;

/**
 * This class acts as a mediator between all of the components of the
 * playlist table.
 */
public final class PlaylistMediator extends AbstractTableMediator {

    /**
     * Instance of singleton access
     */
    private static final PlaylistMediator INSTANCE = new PlaylistMediator();
    public static PlaylistMediator instance() { return INSTANCE; }
    
    /**
     * A lock to use for access to pl stuff.
     */
    private final Object PLAY_LOCK = new Object();
    
    /**
     * The active playlist.
     */
    private PlayList _playList;
    
    /**
     * The temporary playlist - used when songs added but not yet saved.
     */
    private PlayList _tempPL = new PlayList(".temp.m3u.LW");

    /**
     * Whether or not songs meld into each other.
     */
    private boolean _continuous = true;

    /**
     * Whether or not a song is playing right now.
     */
    private boolean _songPlaying = false;

    /**
     * The one-time song that will play next.
     */
    private File _oneTimeSongToPlay = null;
    
	/**
	 * The last playlist that was opened.
	 */
	private File _lastOpenedPlaylist;
	
	/**
	 * The last playlist that was saved.
	 */
	private File _lastSavedPlaylist;    

    /**
     * Listeners so buttons and possibly future right-click menu share.
     */
    ActionListener LOAD_LISTENER;
    ActionListener SAVE_LISTENER;
    ActionListener CONTINUOUS_LISTENER;
    ActionListener SHUFFLE_LISTENER;
    
    /**
     * DATA_MODEL casted to a PlaylistModel so we don't have to do
     * lots of casts.
     */
    private PlaylistModel MODEL;

    /**
     * Build the listeners
     */
    protected void buildListeners() {
        super.buildListeners();
        LOAD_LISTENER = new LoadListener();
        SAVE_LISTENER = new SaveListener();
        CONTINUOUS_LISTENER = new ContinuousListener();
        SHUFFLE_LISTENER = new ShuffleListener();
    }

    /**
     * Add the listeners
     */
    protected void addListeners() {
        super.addListeners();
    }

	/**
	 * Set up the necessary constants.
	 */
	protected void setupConstants() {
		MAIN_PANEL = new PaddedPanel(GUIMediator.getStringResource("PLAYLIST_TITLE"));
		DATA_MODEL = MODEL = new PlaylistModel();
		TABLE = new LimeJTable(DATA_MODEL);
		BUTTON_ROW = (new PlaylistButtons(this)).getComponent();
    }

    /**
     * Update the splash screen
     */
	protected void updateSplashScreen() {
		GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_PLAYLIST_WINDOW"));
    }

	/**
	 * Constructor -- private for Singleton access
	 */
	private PlaylistMediator() {
	    super("PLAYLIST_TABLE");
	    ThemeMediator.addThemeObserver(this);
	}

    // inherit doc comment
    protected JPopupMenu createPopupMenu() {
        return null;
    }
    
    /**
     * Builds the main panel, with checkboxes next to the buttons.
     */
    protected void setupMainPanel() {
        JPanel jp = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        jp.add(getScrolledTablePane(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jp.add(Box.createVerticalStrut(GUIConstants.SEPARATOR), gbc);
        
        gbc.gridy = 2;
        jp.add(buildOptionsPanel(), gbc);

        MAIN_PANEL.add(jp);
        MAIN_PANEL.setMinimumSize(ZERO_DIMENSION);
    }
    
    /**
     * Sets up dnd
     */
    protected void setupDragAndDrop() {
        DragManager.install(TABLE);
    }

	/**
	 * Handles the selection of the specified row in the connection window,
	 * enabling or disabling buttons
	 *
	 * @param row the selected row
	 */
	public void handleSelection(int row) {
	    setButtonEnabled( PlaylistButtons.REMOVE_BUTTON, true );
	}

	/**
	 * Handles the deselection of all rows in the download table,
	 * disabling all necessary buttons and menu items.
	 */
	public void handleNoSelection() {
	    setButtonEnabled( PlaylistButtons.REMOVE_BUTTON, false );
	}

    /**
     * Plays the currently selected song.
     */
    public void handleActionKey() {
        playSong();
    }

    /**
     * Returns the next file to play.
     */
    public File getFileToPlay() {
        File retFile = null;

        synchronized(PLAY_LOCK) {        
            if (_oneTimeSongToPlay != null) {
                retFile = _oneTimeSongToPlay;
                _oneTimeSongToPlay = null;
                return retFile;
            }
    
            if(!isContinuous())
                return null;
                
            retFile = MODEL.getNextSong();
            _songPlaying = true;
        }
        
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                synchronized(PLAY_LOCK) {
                    refresh(); // update the colors on the table.
                    int playIndex = MODEL.getCurrentSongIndex();
                    if(playIndex >= 0)
                        TABLE.ensureRowVisible(playIndex);
                }
            }
        });
        
        return retFile;
    }        
    
    /**
     * Adds a file to the playlist.
     */
    public void addFileToPlaylist(final File f) {
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                synchronized(PLAY_LOCK) {
                    add(f);
                    PlayList pl = getCurrentPlayList();
                    if(pl != null)
                        pl.setSongs(MODEL.getSongs());
                }
            }
        });
    }
    
    /**
     * Adds a bunch of files to the playlist.
     */
    public void addFilesToPlaylist(final File[] fs) {
        if(fs == null || fs.length == 0)
            return;
        
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                synchronized(PLAY_LOCK) {
                    for(int i = 0; i < fs.length; i++)
                        addFileToPlaylist(fs[i]);
                }
            }
        });
    }
    
    /**
     * Determines if a song is currently selected.
     */
    public boolean isSongSelected() { 
        return TABLE.getSelectedRow() != -1;
    }
    
    /**
     * Sets backwards mode on the playlist.
     */
    public void setBackwardsMode() {
        MODEL.setBackwardsMode();
    }
    
    /**
     * Whether or not a song is currently playing from the playlist.
     */
    public boolean isSongPlaying() {
        return _songPlaying;
    }
    
    /**
     * Queues the song for next play.
     */
    public void playSongNext(File f) {
        _oneTimeSongToPlay = f;
    }
    
    /**
     * Whether or not the list should repeat.
     */
    public boolean isContinuous() {
        return _continuous;
    }
    
    /**
     * Notification that play started on a song.
     */
    public void playStarted() {
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                refresh(); // update the colors.
            }
        });
    }
    
    /**
     * Notification that a song has stopped.  'hardStop' true
     * if the user manually pressed stop.
     */
    public void playComplete(boolean hardStop) {
        if(!hardStop)
            return;
        
        _songPlaying = false;
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                refresh(); // update the colors on the table.
            }
        });
    }
    
    /**
     * Constructs the options panel.
     */
    private JPanel buildOptionsPanel() {
        JLabel options = new JLabel(
            GUIMediator.getStringResource("PLAYLIST_OPTIONS_STRING"));
            
        JCheckBox shuffle = new JCheckBox(
            GUIMediator.getStringResource("PLAYLIST_OPTIONS_SHUFFLE"), 
            false);
        shuffle.addActionListener(SHUFFLE_LISTENER);
        
        JCheckBox continuous = new JCheckBox(
            GUIMediator.getStringResource("PLAYLIST_OPTIONS_CONTINUE"), 
            true);
        continuous.addActionListener(CONTINUOUS_LISTENER);
        
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.add(options);
        checkBoxPanel.add(continuous);
        checkBoxPanel.add(shuffle);

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        optionsPanel.add(BUTTON_ROW, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0;
        optionsPanel.add(checkBoxPanel, gbc);
        return optionsPanel;
    }
    
    /**
     * Determines if the file is playable on LimeWire's media player.
     */
    public static boolean isPlayableFile(File f) {
        String ext = FileUtils.getFileExtension(f);
        if(ext == null)
            return false;
        ext = ext.toLowerCase();
        return ext.equals("mp3") || ext.equals("ogg");
    }

	/**
     * Plays the first selected item.
     */
    private void playSong() {
        DataLine line = TABLE.getSelectedDataLine();
        if(line == null)
            return;
            
        File f = ((FileTransfer)line).getFile();
        MODEL.setCurrentSong(f);
        MediaPlayerComponent.playSongImmediately(f);
        _songPlaying = true;
    }
    
    /**
     * @return The playlist to use.  May return null.
     */
    private PlayList getCurrentPlayList() {
        return _playList != null ? _playList : _tempPL;
    }    
    
    /**
     * Loads a playlist.
     */
    private void loadPlaylist() {
		File parentFile = null;
		
		if (_lastOpenedPlaylist != null) {
			String parent = _lastOpenedPlaylist.getParent();
			if(parent != null)	
				parentFile = new File(parent);
		}

		if(parentFile == null)
			parentFile = CommonUtils.getCurrentDirectory();
			
		final File selFile = 
			FileChooserHandler.getInputFile(getComponent(), 
			    "PLAYLIST_DIALOG_OPEN_TITLE", parentFile,
				new PlayListFileFilter());

        // nothing selected? exit.
        if(selFile == null || !selFile.isFile())
            return;
            
            
        String path = selFile.getPath();
        try {
            path = selFile.getCanonicalPath();
        } catch(IOException ignored) {}
        

        PlayList pl = new PlayList(path);
        synchronized(PLAY_LOCK) {
        	_lastOpenedPlaylist = selFile;
            _playList = pl;
            clearTable();
            MODEL.addSongs(_playList);
        }
    }
    
    /**
     * Saves a playlist.
     */
    private void savePlaylist() {
        // get the user to select a new one....
        File suggested;
        if(_lastSavedPlaylist != null)
            suggested = _lastSavedPlaylist;
        else
            suggested = new File(CommonUtils.getCurrentDirectory(), "limewire.m3u");
		
		File selFile =
		    FileChooserHandler.getSaveAsFile(
		        getComponent(), 
		        "PLAYLIST_DIALOG_SAVE_TITLE",
		        suggested,
		        new PlayListFileFilter());
				
        // didn't select a file?  nothing we can do.
        if(selFile == null)
            return;
            
        String path = selFile.getPath();
        try {
            path = selFile.getCanonicalPath();
        } catch(IOException ignored) {}
        // force m3u on the end.
        if(!path.toLowerCase().endsWith(".m3u"))
            path += ".m3u";

        PlayList pl = new PlayList(path);
        synchronized(PLAY_LOCK) {
            _lastSavedPlaylist = new File(path);
            _playList = pl;
            _playList.setSongs(MODEL.getSongs());
            _tempPL = null;
        }
        
        try {
            _playList.save();
        } catch(IOException ignored) {
            //TODO: should message the user that it failed.
        }
    }
    
    /** 
	 * Listener that loads a playlist file.
	 */
    private class LoadListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadPlaylist();
        }
    }
    
    /** 
	 * Listener that saves a playlist file.
	 */
    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            savePlaylist();
        }
    }
    
    /**
     * Listener that toggles the 'continuous' setting.
     */
    private class ContinuousListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AbstractButton b = (AbstractButton)e.getSource();
            _continuous = b.isSelected();
        }
    }
    
    /**
     * Listener that toggles the 'shuffle' setting.
     */
    private class ShuffleListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AbstractButton b = (AbstractButton)e.getSource();
            MODEL.setShuffle(b.isSelected());
        }
    }
    
	/**
	 * <tt>FileFilter</tt> class for only displaying m3u file types in
	 * the directory chooser.
	 */
	private static class PlayListFileFilter extends FileFilter {
		public boolean accept(File f) {
		    return f.isDirectory() ||
		           f.getName().toLowerCase().endsWith("m3u");
		}

		public String getDescription() {
			return GUIMediator.getStringResource("PLAYLIST_FILE_DESCRIPTION");
		}
	}
}
