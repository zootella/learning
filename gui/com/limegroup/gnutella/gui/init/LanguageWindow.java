package com.limegroup.gnutella.gui.init;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.gui.LanguageInfo;
import com.limegroup.gnutella.gui.ResourceManager;

/**
 * This class displays a window to the user allowing them to specify
 * the language LimeWire will use.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class LanguageWindow extends SetupWindow {
    
	/**
	 * The combobox that lists the languages available.
	 */
	private JComboBox _languages;

	/**
	 * Creates the window and its components.
	 */
	LanguageWindow(SetupManager manager) {
		super(manager, "SETUP_LANGUAGE_TITLE", "SETUP_LANGUAGE_LABEL");
		
		_languages = new JComboBox();
		_languages.setFont(new Font("Dialog", Font.PLAIN, 11));
		 LanguageInfo[] langs = LanguageInfo.getLanguages(_languages.getFont());
		_languages.setModel(new DefaultComboBoxModel(langs));
		
		int langIdx = 0;
		String[] wanted = guessLanguage();
		for(int i = 0; i < langs.length; i++) {
		    if(langs[i].matches(wanted)) {
		        langIdx = i;
		        break;
		    }
		}
		
        _languages.setSelectedIndex(langIdx);
        applySettings();
        
        // It is important that the listener is added after the index
        // is set.  Otherwise the listener will call methods that
        // are not ready to be called at this point.
		_languages.addItemListener(new StateListener());
    }
    
    /**
     * Overriden to also add the language options.
     */
    protected void createWindow() {
        super.createWindow();
        
		JPanel mainPanel = new JPanel();
		mainPanel.add(_languages);
		mainPanel.add(Box.createHorizontalGlue());        
		addSetupComponent(mainPanel);
	}

	/**
	 * Overrides applySettings in SetupWindow superclass.
	 * Applies the settings handled in this window.
	 */
	public void applySettings() {
	    LanguageInfo chosen = (LanguageInfo)_languages.getSelectedItem();
	    chosen.apply();
	    ResourceManager.instance().validateLocaleAndFonts();
	}
	
	private class StateListener implements ItemListener {
	    public void itemStateChanged(ItemEvent e) {
	        applySettings();
            _manager.remakeButtons();
            handleWindowOpeningEvent();
            _languages.requestFocus();            
        }
    }
    
    private String[] guessLanguage() {
        String ln = ApplicationSettings.LANGUAGE.getValue();
        String cn = ApplicationSettings.COUNTRY.getValue();
        String vn = ApplicationSettings.LOCALE_VARIANT.getValue();
        
        File file = new File("language.prop");
        if(!file.exists())
            return new String[] { ln, cn, vn };
            
        InputStream in = null;
        BufferedReader reader = null;
        String code = "";
        try {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            code = reader.readLine();
        } catch(IOException ignored) {
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch(IOException ignored) {}
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException ignored) {}
            }
        }
        
        String[] mapped = getLCID(code);
        if(mapped != null)
            return mapped;
        else
            return new String[] { ln, cn, vn };
    }
    
    /**
     * Returns the String[] { languageCode, countryCode, variantCode }
     * for the Windows LCID.
     */
    private String[] getLCID(String code) {
        Map map = new HashMap();
        map.put("1078", new String[] { "af", null, null } );
        map.put("1052", new String[] { "sq", null, null } );
        map.put("5121", new String[] { "ar", null, null } );
        map.put("15361", new String[] { "ar", null, null } );
        map.put("3073", new String[] { "ar", null, null } );
        map.put("2049", new String[] { "ar", null, null } );
        map.put("11265", new String[] { "ar", null, null } );
        map.put("13313", new String[] { "ar", null, null } );
        map.put("12289", new String[] { "ar", null, null } );
        map.put("4097", new String[] { "ar", null, null } );
        map.put("6145", new String[] { "ar", null, null } );
        map.put("8193", new String[] { "ar", null, null } );
        map.put("16385", new String[] { "ar", null, null } );
        map.put("1025", new String[] { "ar", null, null } );
        map.put("10241", new String[] { "ar", null, null } );
        map.put("7169", new String[] { "ar", null, null } );
        map.put("14337", new String[] { "ar", null, null } );
        map.put("9217", new String[] { "ar", null, null } );
        map.put("1069", new String[] { "eu", null, null } );
        map.put("1059", new String[] { "be", null, null } );
        map.put("1093", new String[] { "bn", null, null } );
        map.put("1027", new String[] { "ca", null, null } );
        map.put("3076", new String[] { "zh", null, null } );
        map.put("5124", new String[] { "zh", null, null } );
        map.put("2052", new String[] { "zh", null, null } );
        map.put("4100", new String[] { "zh", null, null } );
        map.put("1028", new String[] { "zh", "TW", null } );
        map.put("1050", new String[] { "hr", null, null } );
        map.put("1029", new String[] { "cs", null, null } );
        map.put("1030", new String[] { "da", null, null } );
        map.put("2067", new String[] { "nl", null, null } );
        map.put("1043", new String[] { "nl", null, null } );
        map.put("3081", new String[] { "en", null, null } );
        map.put("10249", new String[] { "en", null, null } );
        map.put("4105", new String[] { "en", null, null } );
        map.put("9225", new String[] { "en", null, null } );
        map.put("6153", new String[] { "en", null, null } );
        map.put("8201", new String[] { "en", null, null } );
        map.put("5129", new String[] { "en", null, null } );
        map.put("13321", new String[] { "en", null, null } );
        map.put("7177", new String[] { "en", null, null } );
        map.put("11273", new String[] { "en", null, null } );
        map.put("2057", new String[] { "en", null, null } );
        map.put("1033", new String[] { "en", null, null } );
        map.put("12297", new String[] { "en", null, null } );
        map.put("1061", new String[] { "et", null, null } );
        map.put("1035", new String[] { "fi", null, null } );
        map.put("2060", new String[] { "fr", null, null } );
        map.put("11276", new String[] { "fr", null, null } );
        map.put("3084", new String[] { "fr", null, null } );
        map.put("9228", new String[] { "fr", null, null } );
        map.put("12300", new String[] { "fr", null, null } );
        map.put("1036", new String[] { "fr", null, null } );
        map.put("5132", new String[] { "fr", null, null } );
        map.put("13324", new String[] { "fr", null, null } );
        map.put("6156", new String[] { "fr", null, null } );
        map.put("10252", new String[] { "fr", null, null } );
        map.put("4108", new String[] { "fr", null, null } );
        map.put("7180", new String[] { "fr", null, null } );
        map.put("3079", new String[] { "de", null, null } );
        map.put("1031", new String[] { "de", null, null } );
        map.put("5127", new String[] { "de", null, null } );
        map.put("4103", new String[] { "de", null, null } );
        map.put("2055", new String[] { "de", null, null } );
        map.put("1032", new String[] { "el", null, null } );
        map.put("1037", new String[] { "iw", null, null } );
        map.put("1081", new String[] { "hi", null, null } );
        map.put("1038", new String[] { "hu", null, null } );
        map.put("1039", new String[] { "is", null, null } );
        map.put("1057", new String[] { "id", null, null } );
        map.put("1040", new String[] { "it", null, null } );
        map.put("2064", new String[] { "it", null, null } );
        map.put("1041", new String[] { "ja", null, null } );
        map.put("1042", new String[] { "ko", null, null } );
        map.put("1062", new String[] { "lv", null, null } );
        map.put("2110", new String[] { "ms", null, null } );
        map.put("1086", new String[] { "ms", null, null } );
        map.put("1082", new String[] { "mt", null, null } );
        map.put("1044", new String[] { "no", null, null } );
        map.put("2068", new String[] { "nn", null, null } );
        map.put("1045", new String[] { "pl", null, null } );
        map.put("1046", new String[] { "pt", "BR", null } );
        map.put("2070", new String[] { "pt", null, null } );
        map.put("1048", new String[] { "ro", null, null } );
        map.put("2072", new String[] { "ro", null, null } );
        map.put("1049", new String[] { "ru", null, null } );
        map.put("2073", new String[] { "ru", null, null } );
        map.put("3098", new String[] { "sr", null, null } );
        map.put("2074", new String[] { "sr", null, null } );
        map.put("1051", new String[] { "sk", null, null } );
        map.put("1060", new String[] { "sl", null, null } );
        map.put("11274", new String[] { "es", null, null } );
        map.put("16394", new String[] { "es", null, null } );
        map.put("13322", new String[] { "es", null, null } );
        map.put("9226", new String[] { "es", null, null } );
        map.put("5130", new String[] { "es", null, null } );
        map.put("7178", new String[] { "es", null, null } );
        map.put("12298", new String[] { "es", null, null } );
        map.put("17418", new String[] { "es", null, null } );
        map.put("4106", new String[] { "es", null, null } );
        map.put("18442", new String[] { "es", null, null } );
        map.put("3082", new String[] { "es", null, null } );
        map.put("2058", new String[] { "es", null, null } );
        map.put("19466", new String[] { "es", null, null } );
        map.put("6154", new String[] { "es", null, null } );
        map.put("15370", new String[] { "es", null, null } );
        map.put("10250", new String[] { "es", null, null } );
        map.put("20490", new String[] { "es", null, null } );
        map.put("1034", new String[] { "es", null, null } );
        map.put("14346", new String[] { "es", null, null } );
        map.put("8202", new String[] { "es", null, null } );
        map.put("1053", new String[] { "sv", null, null } );
        map.put("2077", new String[] { "sv", null, null } );
        map.put("1097", new String[] { "ta", null, null } );
        map.put("1054", new String[] { "th", null, null } );
        map.put("1055", new String[] { "tr", null, null } );
        map.put("1058", new String[] { "uk", null, null } );
        map.put("1056", new String[] { "ur", null, null } );
        map.put("2115", new String[] { "uz", null, null } );
        map.put("1091", new String[] { "uz", null, null } );
        map.put("1066", new String[] { "vi", null, null } );
        
        return (String[])map.get(code);
    }
}



