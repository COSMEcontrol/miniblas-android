package com.miniblas.api.v1.provider;


import com.miniblas.model.MiniBlasPerfil;
import com.miniblas.perfistence.ormlite.DBHelper;
import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd.SubType;


public class PluginProvider extends OrmLiteSimpleContentProvider<DBHelper>{
	
    @Override
    protected Class<DBHelper> getHelperClass()
    {
        return DBHelper.class;
    }

    @Override
    public boolean onCreate()
    {
        setMatcherController(new MatcherController()
            .add(MiniBlasPerfil.class, SubType.DIRECTORY, "", PluginContract.Profiles.CONTENT_URI_PATTERN_MANY)
            .add(MiniBlasPerfil.class, SubType.ITEM, "#", PluginContract.Profiles.CONTENT_URI_PATTERN_ONE)
            );
        return true;
    }
}
