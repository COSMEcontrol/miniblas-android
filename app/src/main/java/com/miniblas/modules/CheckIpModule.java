package com.miniblas.modules;

import com.miniblas.iu.dialog.alert.AlertDialogEditProfile;
import com.miniblas.iu.dialog.alert.AlertDialogNewProfile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.Module;
import dagger.Provides;

@Module(injects = {AlertDialogNewProfile.class, AlertDialogEditProfile.class,})
public class CheckIpModule{
	private Pattern pattern;
	private Matcher matcher;
	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public CheckIpModule(){
		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}

	public boolean validate(final String ip){
		matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	@Provides
	public CheckIpModule provideCheckIpModule(){
		return this;
	}


}
