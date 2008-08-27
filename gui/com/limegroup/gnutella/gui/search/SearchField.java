package com.limegroup.gnutella.gui.search;

import java.awt.Toolkit;
import java.util.Arrays;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.limegroup.gnutella.gui.AutoCompleteTextField;
import com.limegroup.gnutella.settings.SearchSettings;


/**
 * Specialized class that only allows valid search characters to be
 * entered into text fields.
 */
public class SearchField extends AutoCompleteTextField {

    /**
     * Constant for the array of characters that should not be 
     * allowed in search strings.
     */
    private static final char[] ILLEGAL_CHARS =
        SearchSettings.ILLEGAL_CHARS.getValue();

    /**
     * Constant for the maximum number of bytes to allow in queries.
     */
    private static final int MAX_QUERY_LENGTH =
        SearchSettings.MAX_QUERY_LENGTH.getValue();

    // statically make sure that the array of illegal characters is
    // sorted
    static {
        Arrays.sort(ILLEGAL_CHARS);
    }

    /**
     * Toolkit for sounding beeps when invalid characters are entered.
     */
    private static final Toolkit TOOLKIT = 
        Toolkit.getDefaultToolkit();
        
    /**
     * Creates a new search field.
     */
    public SearchField() {
        super();
    }

    /**
     * Creates a new search field with the specified number of columns.
     *
     * @param columns the number of columns to display in the field
     */
    public SearchField(int columns) {
        super(columns);
    }

    // overridden to filter out invalid search characters
    protected Document createDefaultModel() {
        return new SearchFieldDocument();
    }
    
    /**
     * Helper class that filters out all characters that are not 
     * accepted as search strings.  If the character is not accepted,
     * the system should beep.
     */
    private static class SearchFieldDocument extends PlainDocument {
        public void insertString(int offs,
                                 String str,
                                 AttributeSet a)
            throws BadLocationException {
            if(str == null) {
                super.insertString(offs, str, a);
                return;
            }
            if(offs >= MAX_QUERY_LENGTH) {
                TOOLKIT.beep();
                return;
            }
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;
            for (int i = 0; i<result.length; i++) {
                // if the character is not in the illegal chars, put
                // it in the search string
                if(Arrays.binarySearch(ILLEGAL_CHARS, source[i]) < 0) {
                    result[j++] = source[i];
                } else {
                    // beep to let the user know we won't accept it
                    TOOLKIT.beep();
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}
