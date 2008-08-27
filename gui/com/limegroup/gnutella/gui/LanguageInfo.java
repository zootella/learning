package com.limegroup.gnutella.gui;

import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Contains the information about a single language.
 *
 * Also contains factory methods for retrieving supported languages.
 */
public class LanguageInfo {
    private final String languageCode;
    private final String countryCode;
    private final String variantCode;
    private final String display;
    private final String countryName;
    private final String variantName;
    
    /**
     * Constructs a new LanguageInfo object with the given
     * languageCode, countryCode, variantCode,
     * languageName, countryName, and variantName.
     */
    public LanguageInfo(String lc, String cc, String vc,
                     String ln, String cn, String vn) {
        languageCode = lc.trim();
        countryCode = cc.trim();
        variantCode = vc.trim();
        display = ln.trim();
        countryName = cn.trim();
        variantName = vn.trim();
    }
    
    /**
     * Returns true if the languageCode, countryCode & variantCode
     * match the String[]
     */
    public boolean matches(String[] codes) {
        String lc = codes[0];
        String cc = codes[1];
        String vc = codes[2];
        if(lc == null)
            lc = "";
        if(cc == null)
            cc = "";
        if(vc == null)
            vc = "";
        return languageCode.equals(lc) &&
               countryCode.equals(cc) &&
               variantCode.equals(vc);
    }
    
    /**
     * Determines whether or not this language is the current language.
     */
    public boolean isCurrent() {
        String lc = ApplicationSettings.LANGUAGE.getValue();
        String cc = ApplicationSettings.COUNTRY.getValue();
        String lv = ApplicationSettings.LOCALE_VARIANT.getValue();
        boolean isLV, isCC, isLC;
        isLV = lv == null || lv.equals("") || lv.equals(variantCode);
        isCC = cc == null || cc.equals("") || cc.equals(countryCode);
        isLC = ((lc == null || lc.equals("")) && languageCode.equals("en")) ||
               lc.equals(languageCode);
        return isLV && isCC && isLC;
    }
    
    /**
     * Applies this language code to be the new language of the program.
     */
    public void apply() {
        ApplicationSettings.LANGUAGE.setValue(languageCode);
        ApplicationSettings.COUNTRY.setValue(countryCode);
        ApplicationSettings.LOCALE_VARIANT.setValue(variantCode);
        GUIMediator.resetLocale();
    }        
    
    /**
     * Returns a description of this language.
     * If the variantName is not 'international' or '', then 
     * the display is:
     *    languageName, variantName (countryName)
     * Otherwise, the display is:
     *    languageName (countryName)
     */
    public String toString() {
        if( variantName != null &&
            !variantName.toLowerCase().equals("international") &&
            !variantName.equals("") )
            return display + ", " + variantName + " (" + countryName + ")";
        else
            return display + " (" + countryName + ")";
    }
	
	/**
	 * Returns an array of supported language as a LanguageInfo[],
	 * always having the English language as the first element.
	 *
	 * This will only include languages that can be displayed using
	 * the given font.  If the font is null, all languages are returned.
	 */
	public static LanguageInfo[] getLanguages(Font font) {
	    List langs = null;
	    if( CommonUtils.isTestingVersion() )
	        langs = getLanguagesFromDisk();
	    else
	        langs = getLanguagesFromJar();
        langs.add(0, new LanguageInfo("en", "", "", 
                                      "English", "United States", ""));
                                      
        // Remove languages that cannot be displayed using this font.
        if(font != null && !CommonUtils.isMacOSX()) {
            for(Iterator i  = langs.iterator(); i.hasNext(); ) {
                if(!GUIUtils.canDisplay(font, i.next().toString()))
                    i.remove();
            }
        }
                                      
        return (LanguageInfo[])langs.toArray(new LanguageInfo[langs.size()]);
	}
    
    
    /**
     * Returns the languages as found from the classpath in MessagesBundles.jar
     */
    private static List getLanguagesFromJar() {
        List langs = new LinkedList();
        File jar = CommonUtils.getResourceFile("MessagesBundles.jar");
        if(!jar.exists())
            return langs;
        
        ZipFile zip = null;
        try {
            zip = new ZipFile(jar);
            Enumeration entries = zip.entries();
            while(entries.hasMoreElements()) {
                String name = ((ZipEntry)entries.nextElement()).getName();  
	            if(!name.startsWith("MessagesBundle_") || 
	               name.indexOf("_en") != -1 ||
	               !name.endsWith(".properties"))
	                continue;
	               
	            InputStream in = CommonUtils.getResourceStream(name);
                loadFile(langs, in);
            }
        } catch(IOException e) {
            // oh well.
        } finally {
            if(zip != null)
                try{ zip.close(); } catch(IOException ioe) {}
        }
        
        return langs;
    }
	
	/**
	 * Returns the languages as found in the 
	 * ../lib/messagebundles/MessagesBundle_*.properties files.
	 */
	private static List getLanguagesFromDisk() {
	    List langs = new LinkedList();
	    File lib = new File("../lib/messagebundles");
	    if(!lib.isDirectory())
	        return langs;

	    String[] files = lib.list();
	    for(int i = 0; i < files.length; i++) {
	        if(!files[i].startsWith("MessagesBundle_") ||
	           files[i].indexOf("_en") != -1 ||
	           !files[i].endsWith(".properties"))
	            continue;
	        try {
                InputStream in =
                    new FileInputStream(new File(lib, files[i]));
	            loadFile(langs, in);
            } catch(FileNotFoundException fnfe) {
                // oh well.
            }
        }
        
        return langs;
    }
	
	/**
	 * Loads a single file into a List.
	 */
	private static void loadFile(List langs, InputStream in) {
	    Properties p = new Properties();
        try {
            in = new BufferedInputStream(in);
            p.load(in);

            String lc = p.getProperty("LOCALE_LANGUAGE_CODE");
            String cc = p.getProperty("LOCALE_COUNTRY_CODE");
            String vc = p.getProperty("LOCALE_VARIANT_CODE");
            String ln = p.getProperty("LOCALE_LANGUAGE_NAME");
            String cn = p.getProperty("LOCALE_COUNTRY_NAME");
            String vn = p.getProperty("LOCALE_VARIANT_NAME");
            
            langs.add( new LanguageInfo(lc, cc, vc, ln, cn, vn) );
        // all exceptions are ignorable
        } catch(IOException ignored) {
        } catch(IllegalArgumentException ignored) {
        } catch(NullPointerException ignored) {
        } finally {
            if( in != null )
                try { in.close(); } catch(IOException ioe) {}
        }
    }    
    
}