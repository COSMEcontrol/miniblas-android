package com.miniblas.api.v1.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PluginContract{
	
	public static final String PERMISSION_OPEN_SESSIONS = "com.sonelli.juicessh.api.v1.permission.OPEN_SESSIONS";
	
	public static final class Profiles implements BaseColumns{
		
	    
		public static final String AUTHORITY = "com.miniblas.api.v1.provider";

	    public static final String CONTENT_URI_PATH = "profiles";

	    public static final String MIMETYPE_TYPE = "profiles";

	    public static final int CONTENT_URI_PATTERN_MANY = 1;
	    public static final int CONTENT_URI_PATTERN_ONE = 2;

	    public static final Uri CONTENT_URI = new Uri.Builder()
		    .scheme(ContentResolver.SCHEME_CONTENT)
		    .authority(AUTHORITY)
		    .appendPath(CONTENT_URI_PATH)
		    .build();
	}
}


