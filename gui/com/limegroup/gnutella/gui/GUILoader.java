package com.limegroup.gnutella.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.*;

import com.limegroup.gnutella.bugs.FatalBugManager;


/**
 * This class constructs an <tt>Initializer</tt> instance that constructs
 * all of the necessary classes for the application.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public class GUILoader {
	
	/** 
	 * Creates an <tt>Initializer</tt> instance that constructs the 
	 * necessary classes for the application.
	 *
	 * @param args the array of command line arguments
	 */
	public static void load(String args[], Frame frame) {
        try {
            Java14Notice.showIfNecessary();
	        sanityCheck();
	        Initializer.initialize(args, frame);
        } catch(StartupFailedException sfe) {
            try {
                if(frame != null)
                    frame.dispose();
            } catch(Throwable ignored) {}
            
            showCorruptionError(sfe);
            System.exit(1);
        } catch(Throwable err) {
            try {
                if(frame != null)
                    frame.dispose();
            } catch(Throwable ignored) {}
            
            try {
                FatalBugManager.handleFatalBug(err);
            } catch(Throwable t) {
                showCorruptionError(err);
                System.exit(1);
            }
        }
	}
	
	/**
	 * Display a standardly formatted internal error message
	 * coming from the backend.
	 *
	 * @param message the message to display to the user
	 *
	 * @param err the <tt>Throwable</tt> object containing information 
	 *  about the error
	 */	
	private static final void showCorruptionError(Throwable err) {
		err.printStackTrace();
		final Properties PROPS = System.getProperties();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("LimeWire version @version@");
		pw.print("Java version ");
		pw.print(System.getProperty("java.version", "?"));
		pw.print(" from ");
		pw.println(System.getProperty("java.vendor", "?"));
		pw.print(System.getProperty("os.name", "?"));
		pw.print(" v. ");
		pw.print(System.getProperty("os.version", "?"));
		pw.print(" on ");
		pw.println(System.getProperty("os.arch", "?"));
		Runtime runtime = Runtime.getRuntime();
		pw.println("Free/total memory: "
				   +runtime.freeMemory()+"/"+runtime.totalMemory());
		pw.println();
		
        err.printStackTrace(pw);
        
        pw.println();
        
        pw.println("STARTUP ERROR!");
        pw.println();
        
		File propsFile = new File(getUserSettingsDir(), "limewire.props");
		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(propsFile);
			props.load(fis);
			fis.close();
			// list the properties in the PrintWriter.
			props.list(pw);
		} catch(FileNotFoundException fnfe) {
		} catch(IOException ioe) {
		}
		
        pw.println("");
        pw.println("");
        pw.println("");
		pw.println("FILES IN CURRENT DIRECTORY:");
        File curDir = new File(PROPS.getProperty("user.dir"));
        String[] files = curDir.list();
        for(int i=0; i<files.length; i++) {
            File curFile = new File(curDir, files[i]);
            pw.println(curFile.toString());
            pw.println("LAST MODIFIED: "+curFile.lastModified());
            pw.println("SIZE: "+curFile.length());
            pw.println();
        }

		pw.flush();
		
        displayError(sw.toString());
	}
	
	/**
	 * Gets the settings directory without using CommonUtils.
	 */
    private static File getUserSettingsDir() {
        File dir = new File(System.getProperty("user.home"));
        String os = System.getProperty("os.name").toLowerCase();
        if(os.startsWith("mac os") && os.endsWith("x"))
            return new File(dir, "/Library/Preferences/LimeWire");
        else
            return new File(dir, ".limewire");
    }
        
    
	/**
	 * Displays an internal error with specialized formatting.
	 */
    private static final void displayError(String error) {
        System.out.println("Error: " + error);
		final JDialog DIALOG = new JDialog();
		DIALOG.setModal(true);
		final Dimension DIALOG_DIMENSION = new Dimension(350, 200);
		final Dimension INNER_SIZE = new Dimension(300, 150);
		DIALOG.setSize(DIALOG_DIMENSION);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


		String instr0;
		String instr1;
		String instr2;
		String instr3;
		String instr4;
		String instr5;
		
        instr0 = "One or more necessary files appear to be invalid.";
        instr1 = "This is generally caused by a corrupted installation.";
        instr2 = "Please try downloading and installing LimeWire again.";
        instr3 = "If the problem persists, please copy and paste the";
        instr4 = "message below and send it to: bugs@limewire.com";
        instr5 = "Thank you.";

		JLabel label0 = new JLabel(instr0);
		JLabel label1 = new JLabel(instr1);
		JLabel label2 = new JLabel(instr2);
		JLabel label3 = new JLabel(instr3);
		JLabel label4 = new JLabel(instr4);
		JLabel label5 = new JLabel(instr5);
		
		JPanel labelPanel = new JPanel();
		JPanel innerLabelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		innerLabelPanel.setLayout(new BoxLayout(innerLabelPanel, BoxLayout.Y_AXIS));
		innerLabelPanel.add(label0);
		innerLabelPanel.add(label1);
		innerLabelPanel.add(label2);
		innerLabelPanel.add(label3);
		innerLabelPanel.add(label4);
		innerLabelPanel.add(label5);
		innerLabelPanel.add(Box.createVerticalStrut(6));
		labelPanel.add(innerLabelPanel);
		labelPanel.add(Box.createHorizontalGlue());


        final JTextArea textArea = new JTextArea(error);
        textArea.selectAll();
        textArea.copy();
        textArea.setColumns(50);
        textArea.setEditable(false);
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setBorder(BorderFactory.createEtchedBorder());
		scroller.setPreferredSize(INNER_SIZE);


        JPanel buttonPanel = new JPanel();
        JButton copyButton = new JButton("Copy Report");
        copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    textArea.selectAll();
				textArea.copy();
			}
		});
        JButton quitButton = new JButton("Ok");
        quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				DIALOG.dispose();
			}
		});
        buttonPanel.add(copyButton);
        buttonPanel.add(quitButton);

        mainPanel.add(labelPanel);
        mainPanel.add(scroller);
        mainPanel.add(buttonPanel);

        DIALOG.getContentPane().add(mainPanel);
        DIALOG.pack();        

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = DIALOG.getSize();
		DIALOG.setLocation((screenSize.width - dialogSize.width)/2,
						   (screenSize.height - dialogSize.height)/2);
		DIALOG.setVisible(true);
    }	
	
    /**
     * Determines whether or not specific files exist and are the correct size.
     * If they do not exist or are the incorrect size, 
     * throws MissingResourceException.
     */         
    private static void sanityCheck() throws StartupFailedException {
        File test = new File("gpl.txt");
        boolean isCVS = false;
        
        // If the gpl.txt exists, then we're running off of CVS.
        if( test.exists() && test.isFile() ) {
            isCVS = true;
        }
        // If it doesn't, we're a production version.
        else {
            isCVS = false;
        }
        
        String root = isCVS ? ".."+File.separator+"lib"+File.separator+"jars" : ".";
        
        File logicryptoJar = new File(root, "logicrypto.jar");
        File themesJar = new File(root, "themes.jar");
        File xercesJar = new File(root, "xerces.jar");        
        File updateVer = new File("update.ver");
        File xmlWar = new File("xml.war");
        File messagesBundlesJar = new File("MessagesBundles.jar");
        File id3v2Jar = new File(root, "id3v2.jar");
        
        
        if( !logicryptoJar.exists() || !logicryptoJar.isFile() )
            throw new StartupFailedException("invalid logicrypto.jar");
        
        if( !isCVS && (!themesJar.exists() || !themesJar.isFile()) )
            throw new StartupFailedException("invalid themes.jar");
            
        if( !updateVer.exists() || !updateVer.isFile() )
            throw new StartupFailedException("invalid update.ver");
            
        if( !xercesJar.exists() || !xercesJar.isFile())
            throw new StartupFailedException("invalid xerces.jar");
            
        if( !xmlWar.exists() || !xmlWar.isFile() )
            throw new StartupFailedException("invalid xml.war");
            
        if( !isCVS && (!messagesBundlesJar.exists() || !messagesBundlesJar.isFile()) )
            throw new StartupFailedException("invalid MessagesBundles.jar");            

        if( !id3v2Jar.exists() || !id3v2Jar.isFile() )
            throw new StartupFailedException("invalid id3v2.jar");
            
        // Then do more advanced hash checks.
        try {
            verifyHashes(root);
        } catch(IOException e) {
            throw new StartupFailedException(e.getMessage());
        }
    }
    
    /**
     * Loads up a file 'hashes' and verifies that all the files/hashes
     * in it match the hashes we have.
     */
    private static void verifyHashes(String root) throws IOException, StartupFailedException {
        // Load up the file with the hashes.
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(root, "hashes"));
			props.load(fis);
        } finally {
            if( fis != null ) {
                try {
                    fis.close();
                    fis = null;
                } catch(IOException ignored) {}
            }
        }
        
        //Iterate through each expected hash to verify that
        //the files on disk are not corrupted.
        Enumeration names = props.propertyNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String storedHash = props.getProperty(name);
            String realHash = hash(new File(root, name));
            if(!realHash.equals(storedHash)) {
                throw new StartupFailedException(
                    "file [" + name + "] has hash of [" + realHash +
                    "] instead of expected [" + storedHash + "]");
            }
        }
    }
    
    /**
     * Determines the MD5 hash of the specified file.
     */
    private static String hash(File f) throws IOException {        
		FileInputStream fis = null;		
		try {
		    fis = new FileInputStream(f);
    		MessageDigest md = null;
    		try {
    			md = MessageDigest.getInstance("MD5");
    		} catch(NoSuchAlgorithmException e) {
    		    throw new IOException("Unknown algorithm: MD5");
    		}
            
            byte[] buffer = new byte[16384];
            int read;
            while ((read=fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            
            return toHexString(md.digest());
        } finally {
            if( fis != null ) {
                try {
                    fis.close();
                } catch(IOException ignored) {}
            }
        }
    }
    
    /**
     * Converts a 16-byte array of unsigned bytes
     * to a 32 character hex string.
     */
    private static String toHexString(byte[] data) {
        StringBuffer sb = new StringBuffer(32);
        for(int i = 0; i < data.length; i++) {
            int asInt = ((int)data[i]) & 0x000000FF;
            String x = Integer.toHexString(asInt);
            if(x.length() == 1)
                sb.append("0");
            sb.append(x);
        }
        return sb.toString().toUpperCase();
    }
    
    private static class StartupFailedException extends Exception {
        StartupFailedException(String msg) {
            super(msg);
        } 
        
        StartupFailedException() {
            super();
        }
    }
}
