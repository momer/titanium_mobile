/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2010-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.locale;

import java.util.Locale;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiRHelper;

import android.content.Context;
import android.content.res.Configuration;
import android.telephony.PhoneNumberUtils;

@Kroll.module
public class LocaleModule extends KrollModule
{
	private static final String TAG = "LocaleModule";

	public LocaleModule()
	{
		super();
	}

	public LocaleModule(TiContext tiContext)
	{
		this();
	}
	
	@Kroll.method @Kroll.getProperty
	public String getCurrentLanguage()
	{
		return Locale.getDefault().getLanguage();
	}
	
	@Kroll.method @Kroll.getProperty
	public String getCurrentCountry()
	{
		return Locale.getDefault().getCountry();
	}
	
	@Kroll.method @Kroll.getProperty
	public String getCurrentLocale()
	{
		return TiPlatformHelper.getInstance().getLocale();
	}
	
	@Kroll.method
	public String getCurrencyCode(String localeString) 
	{
		if (localeString == null) {
			return null;
		}
		Locale locale = TiPlatformHelper.getInstance().getLocale(localeString);
		return TiPlatformHelper.getInstance().getCurrencyCode(locale);
	}
	
	@Kroll.method
	public String getCurrencySymbol(String currencyCode)
	{
		return TiPlatformHelper.getInstance().getCurrencySymbol(currencyCode);
	}
	
	@Kroll.method
	public String getLocaleCurrencySymbol(String localeString)
	{
		if (localeString == null) {
			return null;
		}
		Locale locale = TiPlatformHelper.getInstance().getLocale(localeString);
		return TiPlatformHelper.getInstance().getCurrencySymbol(locale);
	}
	
	@SuppressWarnings("deprecation")
	@Kroll.method
	public String formatTelephoneNumber(String telephoneNumber)
	{
		return PhoneNumberUtils.formatNumber(telephoneNumber);
	}
	
	@Kroll.method @Kroll.setProperty
	public void setLanguage(String language) 
	{
		try {
			String[] parts = language.split("-");
			Locale locale = null;
			
			if (parts.length > 1) {
				locale = new Locale(parts[0], parts[1]);
			} else {
				locale = new Locale(parts[0]);
			}
			 
			Locale.setDefault(locale);
			
			Configuration config = new Configuration();
			config.locale = locale;
			
			Context ctx =  TiApplication.getInstance().getBaseContext();
			ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
		} catch (Exception e) {
			Log.e(TAG, "Error trying to set language '" + language + "':", e);
		}
	}

	@Kroll.method  @Kroll.topLevel("L")
	public String getString(String key, @Kroll.argument(optional=true) String defaultValue)
	{
		try {
			int resid = TiRHelper.getResource("string." + key.replace(".","_"));
			if (resid != 0) {
				return TiApplication.getInstance().getString(resid);
			} else {
				return defaultValue;
			}
		} catch (TiRHelper.ResourceNotFoundException e) {
			Log.d(TAG, "Resource string with key '" + key + "' not found.  Returning default value.", Log.DEBUG_MODE);
			return defaultValue;
		} catch (Exception e) {
			Log.e(TAG, "Error trying to get resource string with key '" + key + "':", e);
			return defaultValue;
		}
	}

	@Override
	public String getApiName()
	{
		return "Ti.Locale";
	}
}
