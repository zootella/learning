package com.limegroup.gnutella.gui.xml.editor;

import java.io.File;
import java.util.HashMap;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.xml.LimeXMLUtils;

/**
 * 
 */
public final class MetaEditorUtil {
   
    public static final String AUDIO_SCHEMA = "http://www.limewire.com/schemas/audio.xsd";
    
    public static final String AUDIO = "audios__audio";
    public static final String AUDIO_TITLE = "audios__audio__title__";
    public static final String AUDIO_TRACK = "audios__audio__track__";
    public static final String AUDIO_ARTIST = "audios__audio__artist__";
    public static final String AUDIO_ALBUM = "audios__audio__album__";
    public static final String AUDIO_GENRE = "audios__audio__genre__";
    public static final String AUDIO_COMMENTS = "audios__audio__comments__";
    public static final String AUDIO_YEAR = "audios__audio__year__";
    public static final String AUDIO_TYPE = "audios__audio__type__";
    public static final String AUDIO_LANGUAGE = "audios__audio__language__";
    public static final String AUDIO_SECONDS = "audios__audio__seconds__";
    public static final String AUDIO_SHA1 = "audios__audio__SHA1__";
    public static final String AUDIO_BITRATE = "audios__audio__bitrate__";
    public static final String AUDIO_PRICE = "audios__audio__price__";
    public static final String AUDIO_LINK = "audios__audio__link__";
    public static final String AUDIO_ACTION = "audios__audio__action__";
    public static final String AUDIO_LICENSE = "audios__audio__license__";
    public static final String AUDIO_LICENSETYPE = "audios__audio__licensetype__";
    
    public static final String APPLICATION_SCHEMA = "http://www.limewire.com/schemas/application.xsd";
    
    public static final String APPLICATION = "applications__application";
    public static final String APPLICATION_NAME = "applications__application__name__";
    public static final String APPLICATION_PUBLISHER = "applications__application__publisher__";
    public static final String APPLICATION_PLATFORM = "applications__application__platform__";
    public static final String APPLICATION_LICENSETYPE = "applications__application__licensetype__";
    public static final String APPLICATION_LICENSE = "applications__application__license";
    
    
    public static final String DOCUMENT_SCHEMA = "http://www.limewire.com/schemas/document.xsd";
    
    public static final String DOCUMENT = "documents__document";
    public static final String DOCUMENT_TITLE = "documents__document__title__";
    public static final String DOCUMENT_TOPIC = "documents__document__topic__";
    public static final String DOCUMENT_AUTHOR = "documents__document__author__";
    public static final String DOCUMENT_LICENSE = "documents__document__license__";
    public static final String DOCUMENT_LICENSETYPE = "documents__document__licensetype__";
    
    public static final String IMAGE_SCHEMA = "http://www.limewire.com/schemas/image.xsd";
    
    public static final String IMAGE = "images__image";
    public static final String IMAGE_TITLE = "images__image__title__";
    public static final String IMAGE_DESCRIPTION = "images__image__description__";
    public static final String IMAGE_ARTIST = "images__image__artist__";
    public static final String IMAGE_LICENSE = "images__image__license__";
    public static final String IMAGE_LICENSETYPE = "images__image__licensetype__";
    
    public static final String VIDEO_SCHEMA = "http://www.limewire.com/schemas/video.xsd";
    
    public static final String VIDEO = "videos__video";
    public static final String VIDEO_TITLE = "videos__video__title__";
    public static final String VIDEO_TYPE = "videos__video__type__";
    public static final String VIDEO_YEAR = "videos__video__year__";
    public static final String VIDEO_RATING = "videos__video__rating__";
    public static final String VIDEO_LENGTH = "videos__video__length__";
    public static final String VIDEO_COMMENTS = "videos__video__comments__";
    public static final String VIDEO_LICENSE = "videos__video__license__";
    public static final String VIDEO_LICENSETYPE = "videos__video__licensetype__";
    public static final String VIDEO_HEIGHT = "videos__video__height__";
    public static final String VIDEO_WIDTH = "videos__video__width__";
    public static final String VIDEO_BITRATE = "videos__video__bitrate__";
    public static final String VIDEO_ACTION = "videos__video__action__";
    public static final String VIDEO_DIRECTOR = "videos__video__director__";
    public static final String VIDEO_STUDIO = "videos__video__studio__";
    public static final String VIDEO_LANGUAGE = "videos__video__language__";
    public static final String VIDEO_STARS = "videos__video__stars__";
    public static final String VIDEO_PRODUCER = "videos__video__producer__";
    public static final String VIDEO_SUBTITLES = "videos__video__subtitles__";
    
    private static final HashMap XSD_MESSAGEBUNDLE_BRIDGE = new HashMap();
    
    static {
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO, "META_EDITOR_AUDIO_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_TITLE, "META_EDITOR_TITLE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_ARTIST, "META_EDITOR_ARTIST_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_ALBUM, "META_EDITOR_ALBUM_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_GENRE, "META_EDITOR_GENRE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_YEAR, "META_EDITOR_YEAR_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_TYPE, "META_EDITOR_TYPE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_TRACK, "META_EDITOR_TRACK_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_LANGUAGE, "META_EDITOR_LANGUAGE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_SECONDS, "META_EDITOR_SECONDS_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_BITRATE, "META_EDITOR_BITRATE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_COMMENTS, "META_EDITOR_COMMENTS_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_SHA1, "META_EDITOR_SHA1_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_PRICE, "META_EDITOR_PRICE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_LINK, "META_EDITOR_LINK_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_ACTION, "META_EDITOR_ACTION_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(AUDIO_LICENSE, "META_EDITOR_LICENSE_LABEL");

        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO, "META_EDITOR_VIDEO_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_TITLE, "META_EDITOR_TITLE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_TYPE, "META_EDITOR_TYPE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_YEAR, "META_EDITOR_YEAR_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_RATING, "META_EDITOR_RATING_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_LENGTH, "META_EDITOR_LENGTH_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_COMMENTS, "META_EDITOR_COMMENTS_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_LICENSE, "META_EDITOR_LICENSE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_LICENSETYPE, "META_EDITOR_LICENSETYPE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_ACTION, "META_EDITOR_ACTION_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_DIRECTOR, "META_EDITOR_DIRECTOR_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_STUDIO, "META_EDITOR_STUDIO_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_LANGUAGE, "META_EDITOR_LANGUAGE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_STARS, "META_EDITOR_STARS_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_PRODUCER, "META_EDITOR_PRODUCER_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(VIDEO_SUBTITLES, "META_EDITOR_SUBTITLES_LABEL");
        
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT, "META_EDITOR_DOCUMENT_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT_TITLE, "META_EDITOR_TITLE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT_TOPIC, "META_EDITOR_TOPIC_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT_AUTHOR, "META_EDITOR_AUTHOR_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT_LICENSE, "META_EDITOR_LICENSE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(DOCUMENT_LICENSETYPE, "META_EDITOR_LICENSETYPE_LABEL");
        
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION, "META_EDITOR_APPLICATION_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION_NAME, "META_EDITOR_NAME_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION_PLATFORM, "META_EDITOR_PLATFORM_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION_PUBLISHER, "META_EDITOR_PUBLISHER_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION_LICENSETYPE, "META_EDITOR_LICENSETYPE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(APPLICATION_LICENSE, "META_EDITOR_LICENSE_LABEL");
        
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE, "META_EDITOR_IMAGE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE_TITLE, "META_EDITOR_TITLE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE_DESCRIPTION, "META_EDITOR_DESCRIPTION_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE_ARTIST, "META_EDITOR_ARTIST_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE_LICENSE, "META_EDITOR_LICENSE_LABEL");
        XSD_MESSAGEBUNDLE_BRIDGE.put(IMAGE_LICENSETYPE, "META_EDITOR_LICENSETYPE_LABEL");
    }
    
    public static boolean contains(String resource) {
        return XSD_MESSAGEBUNDLE_BRIDGE.containsKey(resource);
    }
    
    /**
     * 
     */
    public static String getStringResource(String resourceKey) {
        String rscKey = (String)XSD_MESSAGEBUNDLE_BRIDGE.get(resourceKey);
        Assert.that(rscKey != null, "Unknown resourceKey: " + resourceKey);
        return GUIMediator.getStringResource(rscKey);
    }
    
    /**
     * 
     */
    public static String getKind(File file) {
        String name = file.getName();
        
        if (LimeXMLUtils.isMP3File(name)) {
            return GUIMediator.getStringResource("META_EDITOR_MP3_KIND_LABEL");
        } else if (LimeXMLUtils.isM4AFile(name)) {
            return GUIMediator.getStringResource("META_EDITOR_MP4_KIND_LABEL");
        } else if (LimeXMLUtils.isOGGFile(name)) {
            return GUIMediator.getStringResource("META_EDITOR_OGG_KIND_LABEL");
        } else {
            return null;
        }
    }
    
    private MetaEditorUtil() {
    }
}
