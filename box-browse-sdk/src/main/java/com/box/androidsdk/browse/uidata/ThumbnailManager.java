package com.box.androidsdk.browse.uidata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.box.androidsdk.browse.R;
import com.box.androidsdk.content.models.BoxBookmark;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxItem;


/**
 * 
 * This class manages thumbnails to display to users. This class does not do network calls.
 */
public class ThumbnailManager {

    /** The prefix added for thumbnails in this manager. */
    private static final String THUMBNAIL_FILE_PREFIX = "thumbnail_";

    /** The extension added for thumbnails in this manager. */
    private static final String THUMBNAIL_FILE_EXTENSION = ".boxthumbnail";

    /** The path where files in thumbnail should be stored. */
    private File mCacheDirectory;

    /** Handler on ui thread */
    private Handler mHandler;
    
    /** Executor that we will submit our set thumbnail tasks to. */
    private ThreadPoolExecutor thumbnailManagerExecutor;

    private final static HashMap<String, Integer> DEFAULT_ICON_RESORCE_MAP = new HashMap<String, Integer>();

    protected static final String[] DOCUMENTS_EXTENSIONS_ARRAY = {"csv", "doc", "docx", "gdoc", "gsheet", "htm", "html", "msg", "odp", "odt", "ods", "pdf",
            "ppt", "pptx", "rtf", "tsv", "wpd", "xhtml", "xls", "xlsm", "xlsx", "xml", "xsd", "xsl"};
    protected static final String[] PRESENTATION_EXTENSIONS_ARRAY = {"ppt", "pptx"};
    protected static final String[] SPREADSHEET_EXTENSIONS_ARRAY = {"csv", "gsheet", "xls", "xlsm", "xlsx", "xsd", "xsl"};
    protected static final String[] WORD_EXTENSIONS_ARRAY = {"doc", "docx"};

    protected static final String[] AUDIO_EXTENSIONS_ARRAY = {"aac", "aif", "aifc", "aiff", "amr", "au", "flac", "m4a", "mp3", "ra", "wav", "wma"};
    protected static final String[] CODE_EXTENSIONS_ARRAY = {"h", "c", "cp", "cpp", "c++", "cc", "cxx", "m", "strings", "hpp", "h++", "hxx", "mm", "java", "jav", "scala",
            "clj", "coffee", "cl", "css", "diff", "erl", "go", "groovy", "hs", "lhs", "hx", "asp", "aspx", "ejs", "jsp", "html", "htm", "js", "jscript", "javascript", "json",
            "ts", "less", "lua", "markdown", "mdown", "md", "mysql", "sql", "nt", "ocaml", "pas", "pp", "lpr", "dpr", "pascal", "pl", "php", "pig", "plsql", "properties", "ini",
            "py", "r", "rpm", "rst", "rb", "rs", "scheme", "sh", "siv", "sieve", "st", "smarty", "rq", "stex", "tiddlywiki", "vb", "frm", "cs", "vbs", "vm", "v", "vh", "xml",
            "xhtml", "xquery", "xq", "xqy", "yml", "yaml", "z80"};
    protected static final String[] IMAGE_EXTENSIONS_ARRAY = {"ai", "bmp", "dcm", "eps", "jpeg", "jpg", "png", "ps", "psd", "tif", "tiff", "svg", "gif"};
    protected static final String[] VIDEO_EXTENSIONS_ARRAY = {"3g2", "3gp", "avi", "m2v", "m2ts", "m4v", "mkv", "mov", "mp4", "mpeg", "mpg", "ogg", "mts",
            "qt", "wmv"};
    protected static final String[] COMPRESSED_EXTENSIONS_ARRAY = {"zip", "rar", "gz", "tar", "7z", "arc", "ace", "tbz"};
    protected static final String[] INDESIGN_EXTENSIONS_ARRAY = {"indd", "indl", "indt", "indb", "inx", "idml", "pmd"};
    protected static final String[] OBJ_EXTENSIONS_ARRAY = {"obj", "3ds", "x3d"};
    protected static final String[] PHOTOSHOP_EXTENSIONS_ARRAY = {"psd", "psb"};
    protected static final String[] VECTOR_EXTENSIONS_ARRAY = {"eps", "svg"};


    static {
        for (String ext : IMAGE_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_image);
        }
        for (String ext : DOCUMENTS_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_doc);
        }
        for (String ext : PRESENTATION_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_presentation);
        }
        for (String ext : SPREADSHEET_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_spreadsheet);
        }
        for (String ext : WORD_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_word);
        }
        for (String ext : AUDIO_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_audio);
        }
        for (String ext : COMPRESSED_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_compressed);
        }
        for (String ext : CODE_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_code);
        }
        for (String ext : VIDEO_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_movie);
        }
        for (String ext : INDESIGN_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_indesign);
        }
        for (String ext : OBJ_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_obj);
        }
        for (String ext : PHOTOSHOP_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_photoshop);
        }
        for (String ext : VECTOR_EXTENSIONS_ARRAY){
            DEFAULT_ICON_RESORCE_MAP.put(ext, R.drawable.ic_box_browsesdk_vector);
        }
        DEFAULT_ICON_RESORCE_MAP.put("ico", R.drawable.ic_box_browsesdk_icon);

        DEFAULT_ICON_RESORCE_MAP.put("boxnote", R.drawable.ic_box_browsesdk_box_note);
        DEFAULT_ICON_RESORCE_MAP.put("ai", R.drawable.ic_box_browsesdk_illustrator);
        DEFAULT_ICON_RESORCE_MAP.put("pdf", R.drawable.ic_box_browsesdk_pdf);

    }

    /**
     * Constructor.
     * 
     * @param cacheDirectoryPath
     *            the directory to store thumbnail images.
     * @throws java.io.FileNotFoundException
     *             thrown if the directory given does not exist and cannot be created.
     */
    public ThumbnailManager(final String cacheDirectoryPath) throws FileNotFoundException {
        this(new File(cacheDirectoryPath));
    }

    /**
     * Constructor.
     * 
     * @param cacheDirectory
     *            a file representing the directory to store thumbnail images.
     * @throws java.io.FileNotFoundException
     *             thrown if the directory given does not exist and cannot be created.
     */
    public ThumbnailManager(final File cacheDirectory) throws FileNotFoundException {
        mCacheDirectory = cacheDirectory;
        mCacheDirectory.mkdirs();
        if (!mCacheDirectory.exists() || !mCacheDirectory.isDirectory()) {
            throw new FileNotFoundException();
        }
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void populateDefaultResourceMap(){

    }

    /**
     * Sets the best known image for given item into the given view.
     * 
     * @param icon
     *            view to set image for.
     * @param boxItem
     *            the BoxItem used to set the view.
     */
    public void setThumbnailIntoView(final ImageView icon, final BoxItem boxItem) {
        final WeakReference<ImageView> iconHolder = new WeakReference<ImageView>(icon);
        final Resources res = icon.getResources();
        getThumbnailExecutor().execute(new Runnable() {

            public void run() {
                setThumbnail(iconHolder.get(), getDefaultIconResource(boxItem));
                Bitmap cachedIcon = getCachedIcon(boxItem);

                if (cachedIcon != null) {
                    setThumbnail(iconHolder.get(), new BitmapDrawable(res, cachedIcon));
                }
            }
        });

    }

    /**
     * Set the given image resource into given view on ui thread.
     * 
     * @param icon
     *            the ImageView to put drawable into.
     * @param imageRes
     *            the image resource to set into icon.
     */
    private void setThumbnail(final ImageView icon, final int imageRes) {
        mHandler.post(new Runnable() {

            public void run() {
                if (icon != null) {
                    icon.setImageResource(imageRes);
                }
            }
        });
    }

    /**
     * Set the given drawable into given view on ui thread.
     * 
     * @param icon
     *            the ImageView to put drawable into.
     * @param drawable
     *            the drawable to set into icon.
     */
    private void setThumbnail(final ImageView icon, final Drawable drawable) {
        mHandler.post(new Runnable() {

            public void run() {
                if (icon != null) {
                    icon.setImageDrawable(drawable);

                }
            }
        });

    }

    /**
     * Gets the default icon resource depending on what kind of boxItem is being viewed.
     * 
     * @param boxItem
     *            The box item to show to user.
     * @return an integer resource.
     */
    private int getDefaultIconResource(final BoxItem boxItem) {
        if (boxItem instanceof BoxFolder) {
            BoxFolder boxFolder = (BoxFolder) boxItem;
            if (boxFolder.getHasCollaborations() == Boolean.TRUE) {
                if (boxFolder.getIsExternallyOwned() == Boolean.TRUE) {
                    return R.drawable.ic_box_browsesdk_folder_external;
                }
                return R.drawable.ic_box_browsesdk_folder_shared;
            }
            return R.drawable.ic_box_browsesdk_folder_personal;
        } else if (boxItem instanceof BoxBookmark) {
            return R.drawable.ic_box_browsesdk_web_link;
        } else {
            String name = boxItem.getName();
            if (name != null) {
                String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
                Integer resId = DEFAULT_ICON_RESORCE_MAP.get(ext);
                if (resId != null){
                    return resId;
                }
            }
            return R.drawable.ic_box_browsesdk_other;
        }
    }

    /**
     * Gets a bitmap for the item if one exists.
     * 
     * @param boxItem
     *            The box item to show to user.
     * @return an integer resource.
     */
    private Bitmap getCachedIcon(final BoxItem boxItem) {
        File file = getCachedFile(boxItem);
        if (file.exists() && file.isFile()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return bitmap;
        }
        return null;
    }

    /**
     * Returns a file in a determinate location for the given boxItem.
     * 
     * @param boxItem
     *            the box item to generate file or get file for.
     * @return a File object where the icon is located or written to if non existent.
     */
    private File getCachedFile(final BoxItem boxItem) {
        return getThumbnailForFile(boxItem.getId());

    }

    /**
     * Returns a file in a determinate location for the given boxItem.
     * 
     * @param fileId
     *            The id of the box file.
     * @return a File object where the thumbnail is saved to or should be saved to.
     */
    public File getThumbnailForFile(final String fileId) {
        File file = new File(getCacheDirectory(), THUMBNAIL_FILE_PREFIX + fileId + THUMBNAIL_FILE_EXTENSION);
        try{
            file.createNewFile();
        } catch (IOException e){
            // Ignore errors in creating the file to store thumbnails to.
        }
        return file;
    }

    /**
     * @return the cacheDirectory of this thumbnail manager.
     */
    public File getCacheDirectory() {
        return mCacheDirectory;
    }

    /**
     * Convenience method to delete all files in the provided cache directory.
     */
    public void deleteFilesInCacheDirectory() {
        File[] files = mCacheDirectory.listFiles();
        if (files != null) {
            for (int index = 0; index < files.length; index++) {
                if (files[index].isFile()) {
                    files[index].delete();
                }
            }
        }
    }

  

    /**
     * Executor that we will submit our set thumbnail tasks to.
     * 
     * @return executor
     */
    private ThreadPoolExecutor getThumbnailExecutor() {
        if (thumbnailManagerExecutor == null || thumbnailManagerExecutor.isShutdown()) {
            thumbnailManagerExecutor = new ThreadPoolExecutor(4, 10, 3600, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return thumbnailManagerExecutor;
    }

}
