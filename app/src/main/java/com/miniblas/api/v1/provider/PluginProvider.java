package com.miniblas.api.v1.provider;


import com.miniblas.persistence.ormlite.DBHelper;
import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
/**
 *
 * @author A. Azuara
 */
public class PluginProvider extends OrmLiteSimpleContentProvider<DBHelper>{

	public static final int CONTENT_URI_PATTERN_MANY = 1;
	public static final int CONTENT_URI_PATTERN_ONE = 2;

	@Override
	protected Class<DBHelper> getHelperClass(){
		return DBHelper.class;
	}

	@Override
	public boolean onCreate(){
		//setMatcherController(new MatcherController()
		//		.add(MiniBlasProfile.class, MimeTypeVnd.SubType.DIRECTORY, "", CONTENT_URI_PATTERN_MANY)
		//		.add(MiniBlasProfile.class, MimeTypeVnd.SubType.ITEM, "#",CONTENT_URI_PATTERN_ONE));
		return true;
	}
}
