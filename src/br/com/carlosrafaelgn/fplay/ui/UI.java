//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.carlosrafaelgn.fplay.ui;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.carlosrafaelgn.fplay.R;
import br.com.carlosrafaelgn.fplay.playback.Player;
import br.com.carlosrafaelgn.fplay.ui.drawable.BorderDrawable;
import br.com.carlosrafaelgn.fplay.util.ColorUtils;
import br.com.carlosrafaelgn.fplay.util.SerializableMap;

//
//Unit conversions are based on:
//http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/util/TypedValue.java
//
public final class UI {
	public static final int STATE_PRESSED = 1;
	public static final int STATE_FOCUSED = 2;
	public static final int STATE_CURRENT = 4;
	public static final int STATE_SELECTED = 8;
	public static final int STATE_MULTISELECTED = 16;
	public static final int STATE_CHECKED = 32;
	
	public static final int LOCALE_NONE = 0;
	public static final int LOCALE_US = 1;
	public static final int LOCALE_PTBR = 2;
	public static final int LOCALE_RU = 3;
	public static final int LOCALE_UK = 4;
	public static final int LOCALE_MAX = 4;
	
	public static final int THEME_CUSTOM = -1;
	public static final int THEME_BLUE_ORANGE = 0;
	public static final int THEME_BLUE = 1;
	public static final int THEME_ORANGE = 2;
	public static final int THEME_LIGHT = 3;
	
	public static final int MSG_ADD = 0x0001;
	public static final int MSG_PLAY = 0x0002;
	
	public static final String ICON_PREV = "<";
	public static final String ICON_PLAY = "P";
	public static final String ICON_PAUSE = "|";
	public static final String ICON_NEXT = ">";
	public static final String ICON_MENU = "N";
	public static final String ICON_LIST = "l";
	public static final String ICON_MOVE = "M";
	public static final String ICON_REMOVE = "R";
	public static final String ICON_UP = "^";
	public static final String ICON_GOBACK = "_";
	public static final String ICON_SAVE = "S";
	public static final String ICON_LOAD = "L";
	public static final String ICON_FAVORITE_ON = "#";
	public static final String ICON_FAVORITE_OFF = "*";
	public static final String ICON_ADD = "A";
	public static final String ICON_HOME = "H";
	public static final String ICON_LINK = "U";
	public static final String ICON_EQUALIZER = "E";
	public static final String ICON_SETTINGS = "s";
	public static final String ICON_VISUALIZER = "V";
	public static final String ICON_EXIT = "X";
	public static final String ICON_VOLUME0 = "0";
	public static final String ICON_VOLUME1 = "1";
	public static final String ICON_VOLUME2 = "2";
	public static final String ICON_VOLUME3 = "3";
	public static final String ICON_VOLUME4 = "4";
	public static final String ICON_DECREASE_VOLUME = "-";
	public static final String ICON_INCREASE_VOLUME = "+";
	public static final String ICON_FIND = "F";
	public static final String ICON_INFORMATION = "I";
	public static final String ICON_QUESTION = "?";
	public static final String ICON_EXCLAMATION = "!";
	public static final String ICON_SHUFFLE = "h";
	public static final String ICON_REPEAT = "t";
	public static final String ICON_DELETE = "D";
	public static final String ICON_RADIOCHK = "x";
	public static final String ICON_RADIOUNCHK = "o";
	public static final String ICON_OPTCHK = "q";
	public static final String ICON_OPTUNCHK = "Q";
	public static final String ICON_GRIP = "G";
	public static final String ICON_FPLAY = "♫";
	
	public static final int color_transparent = 0x00000000;
	public static int color_window;
	public static int color_control_mode;
	public static int color_list;
	public static int color_menu;
	public static int color_menu_icon;
	public static int color_divider;
	public static int color_highlight;
	public static int color_text_highlight;
	public static int color_text;
	public static int color_text_disabled;
	public static int color_text_listitem;
	public static int color_text_selected;
	public static int color_text_menu;
	public static int color_selected;
	public static int color_selected_multi;
	public static int color_selected_grad_lt;
	public static int color_selected_grad_dk;
	public static int color_selected_border;
	public static int color_selected_pressed;
	public static int color_selected_pressed_border;
	public static int color_focused;
	public static int color_focused_grad_lt;
	public static int color_focused_grad_dk;
	public static int color_focused_border;
	public static int color_focused_pressed;
	public static int color_focused_pressed_border;
	public static ColorStateList colorState_text_white_reactive;
	public static ColorStateList colorState_text_menu_reactive;
	public static ColorStateList colorState_text_reactive;
	public static ColorStateList colorState_text_static;
	public static ColorStateList colorState_text_listitem_static;
	public static ColorStateList colorState_text_selected_static;
	public static ColorStateList colorState_highlight_static;
	public static ColorStateList colorState_text_highlight_static;
	
	public static Typeface iconsTypeface, defaultTypeface;
	
	private static class Gradient {
		private static final Gradient[] gradients = new Gradient[16];
		private static int pos, count;
		public final boolean focused, vertical;
		public final int size;
		public final LinearGradient gradient;
		
		private Gradient(boolean focused, boolean vertical, int size) {
			this.focused = focused;
			this.vertical = vertical;
			this.size = size;
			this.gradient = (focused ? new LinearGradient(0, 0, (vertical ? size : 0), (vertical ? 0 : size), color_focused_grad_lt, color_focused_grad_dk, Shader.TileMode.CLAMP) :
				new LinearGradient(0, 0, (vertical ? size : 0), (vertical ? 0 : size), color_selected_grad_lt, color_selected_grad_dk, Shader.TileMode.CLAMP));
		}
		
		public static void purgeAll() {
			for (int i = gradients.length - 1; i >= 0; i--)
				gradients[i] = null;
			pos = 0;
			count = 0;
		}
		
		public static LinearGradient getGradient(boolean focused, boolean vertical, int size) {
			//a LRU algorithm could be implemented here...
			for (int i = count - 1; i >= 0; i--) {
				if (gradients[i].size == size && gradients[i].focused == focused && gradients[i].vertical == vertical)
					return gradients[i].gradient;
			}
			if (count < 16) {
				pos = count;
				count++;
			} else {
				pos = (pos + 1) & 15;
			}
			final Gradient g = new Gradient(focused, vertical, size);
			gradients[pos] = g;
			return g.gradient;
		}
	}
	
	public static final Rect rect = new Rect();
	public static boolean isLandscape, isLargeScreen, isLowDpiScreen, isDividerVisible, isVerticalMarginLarge, keepScreenOn, displayVolumeInDB, doubleClickMode,
		marqueeTitle, blockBackKey;
	public static int _1dp, _2dp, _4dp, _8dp, _16dp, _2sp, _4sp, _8sp, _16sp, _22sp, _18sp, _14sp, _22spBox, _IconBox, _18spBox, _14spBox, _22spYinBox, _18spYinBox, _14spYinBox,
		strokeSize, thickDividerSize, defaultControlContentsSize, defaultControlSize, usableScreenWidth, usableScreenHeight, screenWidth, screenHeight, densityDpi, forcedOrientation, msgs, msgStartup;
	public static Bitmap icPrev, icPlay, icPause, icNext, icPrevNotif, icPlayNotif, icPauseNotif, icNextNotif, icExitNotif;
	
	private static String emptyListString;
    private static int emptyListStringHalfWidth, forcedLocale, currentLocale, theme;
    private static boolean alternateTypefaceActive, useAlternateTypeface, fullyInitialized;
	private static Toast internalToast;
	
	public static float density, scaledDensity, xdpi_1_72;
	
	public static final Paint fillPaint;
	private static final TextPaint textPaint;
	
	static {
		fillPaint = new Paint();
		fillPaint.setDither(false);
		fillPaint.setAntiAlias(false);
		fillPaint.setStyle(Paint.Style.FILL);
		textPaint = new TextPaint();
		textPaint.setDither(false);
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setColor(color_text);
		textPaint.measureText("FPlay");
		loadLightTheme();
	}
	
	public static boolean isUsingAlternateTypeface() {
		return useAlternateTypeface;
	}
	
	public static void setUsingAlternateTypeface(Context context, boolean useAlternateTypeface) {
		UI.useAlternateTypeface = useAlternateTypeface;
		if (useAlternateTypeface && !isCurrentLocaleCyrillic()) {
			if (defaultTypeface == null || !alternateTypefaceActive) {
				alternateTypefaceActive = true;
				try {
					defaultTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenDyslexicRegular.otf");
				} catch (Throwable ex) {
					UI.useAlternateTypeface = false;
					alternateTypefaceActive = false;
					defaultTypeface = Typeface.DEFAULT;
				}
			}
		} else {
			alternateTypefaceActive = false;
			defaultTypeface = Typeface.DEFAULT;
		}
		textPaint.setTypeface(defaultTypeface);
		//Font Metrics in Java OR How, the hell, Should I Position This Font?!
		//http://blog.evendanan.net/2011/12/Font-Metrics-in-Java-OR-How-the-hell-Should-I-Position-This-Font
		textPaint.setTextSize(_22sp);
		final FontMetrics fm = textPaint.getFontMetrics();
		_22spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_22spYinBox = _22spBox - (int)(fm.descent);
		textPaint.setTextSize(_18sp);
		textPaint.getFontMetrics(fm);
		_18spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_18spYinBox = _18spBox - (int)(fm.descent);
		textPaint.setTextSize(_14sp);
		textPaint.getFontMetrics(fm);
		_14spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_14spYinBox = _14spBox - (int)(fm.descent);
		emptyListString = context.getText(R.string.empty_list).toString();
		emptyListStringHalfWidth = measureText(emptyListString, _22sp) >> 1;
		_IconBox = Math.min(spToPxI(24), _22spBox); //both descent and ascent of iconsTypeface are 0!
	}
	
	public static Locale getLocaleFromCode(int localeCode) {
		final Locale l = Locale.getDefault();
		switch (localeCode) {
		case LOCALE_US:
			if (!l.equals(Locale.US))
				return Locale.US;
			break;
		case LOCALE_PTBR:
			if (!"pt".equals(l.getLanguage()))
				return new Locale("pt", "BR");
			break;
		case LOCALE_RU:
			if (!"ru".equals(l.getLanguage()))
				return new Locale("ru", "RU");
			break;
		case LOCALE_UK:
			if (!"uk".equals(l.getLanguage()))
				return new Locale("uk");
			break;
		}
		return l;
	}
	
	public static String getLocaleDescriptionFromCode(Context context, int localeCode) {
		switch (localeCode) {
		case LOCALE_US:
			return "English";
		case LOCALE_PTBR:
			return "Português (Brasil)";
		case LOCALE_RU:
			return "Русский";
		case LOCALE_UK:
			return "Українська";
		}
		return context.getText(R.string.standard_language).toString();
	}
	
	public static int getCurrentLocale(Context context) {
		try {
			final String l = context.getResources().getConfiguration().locale.getLanguage().toLowerCase(Locale.US);
			if ("pt".equals(l))
				return LOCALE_PTBR;
			if ("ru".equals(l))
				return LOCALE_RU;
			if ("uk".equals(l))
				return LOCALE_UK;
		} catch (Throwable ex) {
		}
		return LOCALE_US;
	}
	
	public static boolean isCurrentLocaleCyrillic() {
		return ((currentLocale == LOCALE_RU) || (currentLocale == LOCALE_UK));
	}
	
	public static int getForcedLocale() {
		return forcedLocale;
	}
	
	public static boolean setForcedLocale(Context context, int localeCode) {
		if (localeCode < 0 || localeCode > LOCALE_MAX)
			localeCode = LOCALE_NONE;
		final Resources res = context.getResources();
		if (forcedLocale == 0 && localeCode == 0) {
			currentLocale = getCurrentLocale(context);
			return false;
		}
		try {
			final Locale l = getLocaleFromCode(localeCode);
			final Configuration cfg = new Configuration(res.getConfiguration());
			cfg.locale = l;
			res.updateConfiguration(cfg, res.getDisplayMetrics());
			forcedLocale = localeCode;
			currentLocale = ((localeCode == 0) ? getCurrentLocale(context) : localeCode);
		} catch (Throwable ex) {
			currentLocale = getCurrentLocale(context);
		}
		if (fullyInitialized) {
			setUsingAlternateTypeface(context, useAlternateTypeface);
			return true;
		}
		return false;
	}
	
	public static void setUsingAlternateTypefaceAndForcedLocale(Context context, boolean useAlternateTypeface, int localeCode) {
		UI.useAlternateTypeface = useAlternateTypeface;
		if (!setForcedLocale(context, localeCode))
			setUsingAlternateTypeface(context, useAlternateTypeface);
	}
	
	private static void initializeScreenDimensions(Display display, DisplayMetrics outDisplayMetrics) {
		display.getMetrics(outDisplayMetrics);
		screenWidth = outDisplayMetrics.widthPixels;
		screenHeight = outDisplayMetrics.heightPixels;
		usableScreenWidth = screenWidth;
		usableScreenHeight = screenHeight;
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private static void initializeScreenDimensions14(Display display, DisplayMetrics outDisplayMetrics) {
		try {
			screenWidth = (Integer)Display.class.getMethod("getRawWidth").invoke(display);
			screenHeight = (Integer)Display.class.getMethod("getRawHeight").invoke(display);
		} catch (Throwable ex) {
			initializeScreenDimensions(display, outDisplayMetrics);
			return;
		}
		display.getMetrics(outDisplayMetrics);
		usableScreenWidth = outDisplayMetrics.widthPixels;
		usableScreenHeight = outDisplayMetrics.heightPixels;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static void initializeScreenDimensions17(Display display, DisplayMetrics outDisplayMetrics) {
		display.getMetrics(outDisplayMetrics);
		usableScreenWidth = outDisplayMetrics.widthPixels;
		usableScreenHeight = outDisplayMetrics.heightPixels;
		display.getRealMetrics(outDisplayMetrics);
		screenWidth = outDisplayMetrics.widthPixels;
		screenHeight = outDisplayMetrics.heightPixels;
	}
	
	public static void loadWidgetRelatedSettings(Context context) {
		if (fullyInitialized)
			return;
		final SerializableMap opts = Player.loadConfigFromFile(context);
		//I know, this is ugly... I'll fix it one day...
		setForcedLocale(context, opts.getInt(0x001E, LOCALE_NONE));
	}
	
	public static void initialize(Context context) {
		fullyInitialized = true;
		if (iconsTypeface == null)
			iconsTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/icons.ttf");
		final Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			initializeScreenDimensions17(display, displayMetrics);
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			initializeScreenDimensions14(display, displayMetrics);
		else
			initializeScreenDimensions(display, displayMetrics);
		density = displayMetrics.density;
		densityDpi = displayMetrics.densityDpi;
		scaledDensity = displayMetrics.scaledDensity;
		xdpi_1_72 = displayMetrics.xdpi * (1.0f / 72.0f);
		//apparently, the display metrics returned by Resources.getDisplayMetrics()
		//is not the same as the one returned by Display.getMetrics()/getRealMetrics()
		final float sd = context.getResources().getDisplayMetrics().scaledDensity;
		if (sd > 0)
			scaledDensity = sd;
		//improved detection for tablets, based on:
		//http://developer.android.com/guide/practices/screens_support.html#DeclaringTabletLayouts
		//(There is also the solution at http://stackoverflow.com/questions/11330363/how-to-detect-device-is-android-phone-or-android-tablet
		//but the former link says it is deprecated...)
		isLargeScreen = ((screenWidth >= dpToPxI(600)) && (screenHeight >= dpToPxI(600)));
		isLandscape = (screenWidth >= screenHeight);
		isLowDpiScreen = (displayMetrics.densityDpi < 160);
		_1dp = dpToPxI(1);
		strokeSize = (_1dp + 1) >> 1;
		thickDividerSize = ((_1dp >= 2) ? _1dp : 2);
		_2dp = dpToPxI(2);
		_4dp = dpToPxI(4);
		_8dp = dpToPxI(8);
		_16dp = dpToPxI(16);
		_2sp = spToPxI(2);
		_4sp = spToPxI(4);
		_8sp = spToPxI(8);
		_16sp = spToPxI(16);
		_22sp = spToPxI(22);
		_18sp = spToPxI(18);
		_14sp = spToPxI(14);
		defaultControlContentsSize = dpToPxI(32);
		defaultControlSize = defaultControlContentsSize + (UI._8sp << 1);
		if (!setForcedLocale(context, forcedLocale))
			setUsingAlternateTypeface(context, useAlternateTypeface);
	}
	
	public static void preparePlaybackIcons(Context context) {
		if (icPrev != null)
			return;
		if (iconsTypeface == null)
			initialize(context);
	    final Canvas c = new Canvas();
	    textPaint.setTypeface(iconsTypeface);
		textPaint.setColor(0xff000000);
		textPaint.setTextSize(defaultControlContentsSize);
		icPrev = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPrev);
	    c.drawText(ICON_PREV, 0, defaultControlContentsSize, textPaint);
	    icPlay = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPlay);
	    c.drawText(ICON_PLAY, 0, defaultControlContentsSize, textPaint);
	    icPause = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPause);
	    c.drawText(ICON_PAUSE, 0, defaultControlContentsSize, textPaint);
	    icNext = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icNext);
	    c.drawText(ICON_NEXT, 0, defaultControlContentsSize, textPaint);
	    //reset to the original state
	    textPaint.setTypeface(defaultTypeface);
		textPaint.setColor(color_text);
		textPaint.measureText("FPlay");
	}
	
	public static void prepareNotificationPlaybackIcons(Context context) {
		if (icPrevNotif != null)
			return;
		if (iconsTypeface == null)
			initialize(context);
	    final Canvas c = new Canvas();
	    textPaint.setTypeface(iconsTypeface);
		textPaint.setColor(0xffffffff);
		textPaint.setTextSize(defaultControlContentsSize);
		icPrevNotif = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPrevNotif);
	    c.drawText(ICON_PREV, 0, defaultControlContentsSize, textPaint);
	    icPlayNotif = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPlayNotif);
	    c.drawText(ICON_PLAY, 0, defaultControlContentsSize, textPaint);
	    icPauseNotif = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icPauseNotif);
	    c.drawText(ICON_PAUSE, 0, defaultControlContentsSize, textPaint);
	    icNextNotif = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icNextNotif);
	    c.drawText(ICON_NEXT, 0, defaultControlContentsSize, textPaint);
	    icExitNotif = Bitmap.createBitmap(defaultControlContentsSize, defaultControlContentsSize, Bitmap.Config.ARGB_8888);
	    c.setBitmap(icExitNotif);
	    c.drawText(ICON_EXIT, 0, defaultControlContentsSize, textPaint);
	    //reset to the original state
	    textPaint.setTypeface(defaultTypeface);
		textPaint.setColor(color_text);
		textPaint.measureText("FPlay");
	}
	
	public static float pxToDp(float px) {
		return px / density;
	}
	
	public static float pxToSp(float px) {
		return px / scaledDensity;
	}
	
	public static float pxToPt(float px) {
		return px / xdpi_1_72;
	}
	
	public static float dpToPx(float dp) {
		return dp * density;
	}
	
	public static float spToPx(float sp) {
		return sp * scaledDensity;
	}
	
	public static float ptToPx(float pt) {
		return pt * xdpi_1_72;
	}
	
	public static int dpToPxI(float dp) {
		return (int)((dp * density) + 0.5f);
	}
	
	public static int spToPxI(float sp) {
		return (int)((sp * scaledDensity) + 0.5f);
	}
	
	public static int ptToPxI(float pt) {
		return (int)((pt * xdpi_1_72) + 0.5f);
	}
	
	private static void finishLoadingTheme() {
		color_selected = ColorUtils.blend(color_selected_grad_lt, color_selected_grad_dk, 0.5f);
		color_focused = ColorUtils.blend(color_focused_grad_lt, color_focused_grad_dk, 0.5f);
		color_selected_multi = ColorUtils.blend(color_selected, color_list, 0.7f);
		color_selected_pressed_border = color_selected_border;
		color_focused_pressed_border = color_focused_border;
		colorState_text_white_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, 0xffffffff });
		colorState_text_menu_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text_menu });
		colorState_text_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text });
		colorState_text_static = ColorStateList.valueOf(color_text);
		colorState_text_listitem_static = ColorStateList.valueOf(color_text_listitem);
		colorState_text_selected_static = ColorStateList.valueOf(color_text_selected);
		colorState_highlight_static = ColorStateList.valueOf(color_highlight);
		colorState_text_highlight_static = ColorStateList.valueOf(color_text_highlight);
	}
	
	public static void loadBlueOrangeTheme() {
		color_window = 0xff303030;
		color_control_mode = 0xff000000;
		color_list = 0xff080808;
		color_menu = 0xffffffff;
		color_menu_icon = 0xff555555;
		color_divider = 0xff464646;
		color_highlight = 0xfffad35a;
		color_text_highlight = 0xff000000;
		color_text = 0xffffffff;
		color_text_disabled = 0xff959595;
		color_text_listitem = 0xffffffff;
		color_text_selected = 0xff000000;
		color_text_menu = 0xff000000;
		//color_selected = 0xff99c6f2; //0xffadd6fd;
		//color_selected_multi = 0xff779aba; //60% #add6fd over #000000 (adjusted to comply with minimum contrast ratio according to WCAG 2.0)
		color_selected_grad_lt = 0xffd1e8ff;
		color_selected_grad_dk = 0xff5da2e3;
		color_selected_border = 0xff518ec2;
		color_selected_pressed = 0xffcfe1ff;
		//color_selected_pressed_border = 0xff4981b0; //darker version of #518ec2
		//color_focused = 0xfffad35a;
		color_focused_grad_lt = 0xfff7eb6a;
		color_focused_grad_dk = 0xfffeb645;
		color_focused_border = 0xffad9040;
		color_focused_pressed = 0xffffeed4;
		//color_focused_pressed_border = 0xff94671e; //darker version of #ad9040
		finishLoadingTheme();
	}
	
	public static void loadBlueTheme() {
		color_window = 0xff303030;
		color_control_mode = 0xff000000;
		color_list = 0xff080808;
		color_menu = 0xffffffff;
		color_menu_icon = 0xff555555;
		color_divider = 0xff464646;
		color_highlight = 0xff94c0ff;
		color_text_highlight = 0xff000000;
		color_text = 0xffffffff;
		color_text_disabled = 0xff959595;
		color_text_listitem = 0xffffffff;
		color_text_selected = 0xff000000;
		color_text_menu = 0xff000000;
		//color_selected = 0xff99c6f2; //0xffadd6fd;
		//color_selected_multi = 0xff779aba; //60% #add6fd over #000000 (adjusted to comply with minimum contrast ratio according to WCAG 2.0)
		color_selected_grad_lt = 0xffd1e8ff;
		color_selected_grad_dk = 0xff5da2e3;
		color_selected_border = 0xff518ec2;
		color_selected_pressed = 0xffcfe1ff;
		//color_selected_pressed_border = 0xff4981b0; //darker version of #518ec2
		//color_focused = 0xfffad35a;
		color_focused_grad_lt = 0xfff7eb6a;
		color_focused_grad_dk = 0xfffeb645;
		color_focused_border = 0xffad9040;
		color_focused_pressed = 0xffffeed4;
		//color_focused_pressed_border = 0xff94671e; //darker version of #ad9040
		finishLoadingTheme();
	}
	
	public static void loadOrangeTheme() {
		color_window = 0xff303030;
		color_control_mode = 0xff000000;
		color_list = 0xff080808;
		color_menu = 0xffffffff;
		color_menu_icon = 0xff555555;
		color_divider = 0xff464646;
		color_highlight = 0xfffad35a;
		color_text_highlight = 0xff000000;
		color_text = 0xffffffff;
		color_text_disabled = 0xff959595;
		color_text_listitem = 0xffffffff;
		color_text_selected = 0xff000000;
		color_text_menu = 0xff000000;
		//color_selected = 0xfffad35a;
		//color_selected_multi = 0xffad954e; //60% #fad35a over #000000 (adjusted to comply with minimum contrast ratio according to WCAG 2.0)
		color_selected_grad_lt = 0xfff7eb6a;
		color_selected_grad_dk = 0xfffeb645;
		color_selected_border = 0xffad9040;
		color_selected_pressed = 0xffffeed4;
		//color_selected_pressed_border = 0xff94671e; //darker version of #ad9040
		//color_focused = 0xff99c6f2; //0xffadd6fd;
		color_focused_grad_lt = 0xffd1e8ff;
		color_focused_grad_dk = 0xff5da2e3;
		color_focused_border = 0xff518ec2;
		color_focused_pressed = 0xffcfe1ff;
		//color_focused_pressed_border = 0xff4981b0; //darker version of #518ec2
		finishLoadingTheme();
	}
	
	public static void loadLightTheme() {
		color_window = 0xffe0e0e0;
		color_control_mode = 0xffe0e0e0;
		color_list = 0xfff2f2f2;
		color_menu = 0xffffffff;
		color_menu_icon = 0xff555555;
		color_divider = 0xff9f9f9f;
		color_highlight = 0xff0045e0;
		color_text_highlight = 0xffffffff;
		color_text = 0xff000000;
		color_text_disabled = 0xff959595;
		color_text_listitem = 0xff000000;
		color_text_selected = 0xff000000;
		color_text_menu = 0xff000000;
		//color_selected = 0xff99c6f2; //0xffadd6fd;
		//color_selected_multi = 0xff5da2e3; //60% #add6fd over #f2f2f2 (adjusted to comply with minimum contrast ratio according to WCAG 2.0)
		color_selected_grad_lt = 0xffd1e8ff;
		color_selected_grad_dk = 0xff5da2e3;
		color_selected_border = 0xff518ec2;
		color_selected_pressed = 0xffcfe1ff;
		//color_selected_pressed_border = 0xff4981b0; //darker version of #518ec2
		//color_focused = 0xfffad35a;
		color_focused_grad_lt = 0xfff7eb6a;
		color_focused_grad_dk = 0xfffeb645;
		color_focused_border = 0xffad9040;
		color_focused_pressed = 0xffffeed4;
		//color_focused_pressed_border = 0xff94671e; //darker version of #ad9040
		finishLoadingTheme();
	}
	
	public static String getThemeString(Context context, int theme) {
		switch (theme) {
		case THEME_CUSTOM:
			return context.getText(R.string.custom).toString();
		case THEME_BLUE:
			return context.getText(R.string.blue).toString();
		case THEME_ORANGE:
			return context.getText(R.string.orange).toString();
		case THEME_LIGHT:
			return context.getText(R.string.light).toString();
		default:
			return context.getText(R.string.blue_orange).toString();
		}
	}
	
	public static int getTheme() {
		return theme;
	}
	
	public static void setTheme(int theme) {
		UI.theme = theme;
		Gradient.purgeAll();
		switch (theme) {
		//case THEME_CUSTOM:
		//	//custom
		//	break;
		case THEME_BLUE:
			loadBlueTheme();
			break;
		case THEME_ORANGE:
			loadOrangeTheme();
			break;
		case THEME_LIGHT:
			loadLightTheme();
			break;
		default:
			UI.theme = THEME_BLUE_ORANGE;
			loadBlueOrangeTheme();
			break;
		}
	}
	
	public static void showNextStartupMsg(final Activity activity) {
		if (msgStartup >= 1) {
			msgStartup = 1;
			return;
		}
		int title = R.string.new_setting;
		String content = "";
		if (msgStartup <= 0) {
			msgStartup = 1;
			content = activity.getText(R.string.there_is_a_new_setting).toString() + " " + activity.getText(R.string.color_scheme).toString() + "\n\n" + activity.getText(R.string.check_it_out).toString();
		}
		UI.prepareDialogAndShow((new AlertDialog.Builder(activity))
		.setTitle(activity.getText(title))
		.setMessage(content)
		.setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showNextStartupMsg(activity);
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				showNextStartupMsg(activity);
			}
		})
		.create());
	}
	
	public static boolean showMsg(Activity activity, int msg) {
		if ((msgs & msg) != 0)
			return false;
		int title, content;
		switch (msg) {
		case MSG_ADD:
			title = R.string.add;
			content = R.string.msg_add;
			break;
		case MSG_PLAY:
			title = R.string.play;
			content = R.string.msg_play;
			break;
		default:
			return false;
		}
		UI.prepareDialogAndShow((new AlertDialog.Builder(activity))
		.setTitle(activity.getText(title))
		.setMessage(activity.getText(content))
		.setPositiveButton(R.string.got_it, null)
		.create());
		msgs |= msg;
		return true;
	}
	
	public static String ellipsizeText(String text, int size, int width) {
		textPaint.setTextSize(size);
		return TextUtils.ellipsize(text, textPaint, width, TruncateAt.END).toString();
	}
	
	public static int measureText(String text, int size) {
		textPaint.setTextSize(size);
		return (int)(textPaint.measureText(text) + 0.5f);
	}
	
	public static void drawText(Canvas canvas, String text, int color, int size, int x, int y) {
		textPaint.setColor(color);
		textPaint.setTextSize(size);
		canvas.drawText(text, x, y, textPaint);
	}
	
	public static void drawEmptyListString(Canvas canvas) {
		textPaint.setColor(color_text_disabled);
		textPaint.setTextSize(_22sp);
		canvas.drawText(emptyListString, (UI.rect.right >> 1) - emptyListStringHalfWidth, (UI.rect.bottom >> 1) - (_18spBox >> 1) + _18spYinBox, textPaint);
	}
	
	public static void fillRect(Canvas canvas, int fillColor, Rect rect) {
		fillPaint.setColor(fillColor);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, fillPaint);
	}
	
	public static void fillRect(Canvas canvas, int fillColor, Rect rect, int insetX, int insetY) {
		fillPaint.setColor(fillColor);
		canvas.drawRect(rect.left + insetX, rect.top + insetY, rect.right - insetX, rect.bottom - insetY, fillPaint);
	}
	
	public static void fillRect(Canvas canvas, Shader shader, Rect rect) {
		fillPaint.setShader(shader);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, fillPaint);
		fillPaint.setShader(null);
	}
	
	public static void fillRect(Canvas canvas, Shader shader, Rect rect, int insetX, int insetY) {
		fillPaint.setShader(shader);
		canvas.drawRect(rect.left + insetX, rect.top + insetY, rect.right - insetX, rect.bottom - insetY, fillPaint);
		fillPaint.setShader(null);
	}
	
	public static void strokeRect(Canvas canvas, int strokeColor, Rect rect, int thickness) {
		fillPaint.setColor(strokeColor);
		final int l = rect.left, t = rect.top, r = rect.right, b = rect.bottom;
		canvas.drawRect(l, t, r, t + thickness, fillPaint);
		canvas.drawRect(l, b - thickness, r, b, fillPaint);
		canvas.drawRect(l, t + thickness, l + thickness, b - thickness, fillPaint);
		canvas.drawRect(r - thickness, t + thickness, r, b - thickness, fillPaint);
	}
	
	public static int getBorderColor(int state) {
		if ((state & STATE_PRESSED) != 0)
			return (((state & STATE_FOCUSED) != 0) ? color_focused_pressed_border : color_selected_pressed_border);
		if ((state & STATE_FOCUSED) != 0)
			return color_focused_border;
		if ((state & STATE_SELECTED) != 0)
			return color_selected_border;
		return 0;
	}
	
	public static void drawBgBorderless(Canvas canvas, int state, Rect rect) {
		if ((state & ~STATE_CURRENT) != 0) {
			if ((state & STATE_PRESSED) != 0)
				fillRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed : color_selected_pressed, rect);
			else if ((state & (STATE_SELECTED | STATE_FOCUSED)) != 0)
				fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect);
			else if ((state & STATE_MULTISELECTED) != 0)
				fillRect(canvas, color_selected_multi, rect);
		}
	}
	
	private static void drawDivider(Canvas canvas, Rect rect) {
		fillPaint.setColor(color_divider);
		final int top = rect.top;
		rect.top = rect.bottom - strokeSize;
		canvas.drawRect(rect, fillPaint);
		rect.top = top;
	}
	
	public static void drawBg(Canvas canvas, int state, Rect rect, boolean squareItem, boolean dividerAllowed) {
		dividerAllowed &= isDividerVisible;
		if ((state & ~STATE_CURRENT) != 0) {
			if ((state & STATE_PRESSED) != 0) {
				strokeRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed_border : color_selected_pressed_border, rect, strokeSize);
				fillRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed : color_selected_pressed, rect, strokeSize, strokeSize);
				return;
			} else if ((state & (STATE_SELECTED | STATE_FOCUSED)) != 0) {
				if (squareItem) {
					strokeRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_border : color_selected_border, rect, strokeSize);
					fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect, strokeSize, strokeSize);
				} else {
					fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect);
				}
			} else if ((state & STATE_MULTISELECTED) != 0) {
				fillRect(canvas, color_selected_multi, rect);
			}
		}
		if (!squareItem && dividerAllowed)
			drawDivider(canvas, rect);
	}
	
	public static int handleStateChanges(int state, boolean pressed, boolean focused, View view) {
		boolean r = false;
		final boolean op = ((state & UI.STATE_PRESSED) != 0), of = ((state & UI.STATE_FOCUSED) != 0);
		if (op != pressed) {
			if (pressed)
				state |= UI.STATE_PRESSED;
			else
				state &= ~UI.STATE_PRESSED;
			r = true;
		}
		if (of != focused) {
			if (focused)
				state |= UI.STATE_FOCUSED;
			else
				state &= ~UI.STATE_FOCUSED;
			r = true;
		}
		if (r)
			view.invalidate();
		return state;
	}
	
	public static void smallText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _14sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void smallTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _14sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void mediumText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _18sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void mediumTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _18sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void largeText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _22sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void largeTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _22sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void toast(Context context, Throwable ex) {
		String s = ex.getMessage();
		if (s != null && s.length() > 0)
			s = context.getText(R.string.error).toString() + " " + s;
		else
			s = context.getText(R.string.error).toString() + " " + ex.getClass().getName();
		toast(context, s);
	}
	
	public static void toast(Context context, int resId) {
		toast(context, context.getText(resId).toString());
	}
	
	@SuppressWarnings("deprecation")
	public static void prepareNotificationViewColors(TextView view) {
		view.setTextColor(UI.colorState_text_highlight_static);
		view.setBackgroundDrawable(new BorderDrawable(ColorUtils.blend(color_highlight, 0, 0.5f), color_highlight, true, true, true, true));
	}
	
	public static void toast(Context context, String text) {
		if (internalToast == null) {
			final Toast t = new Toast(context);
			final TextView v = new TextView(context);
			mediumText(v);
			prepareNotificationViewColors(v);
			v.setGravity(Gravity.CENTER);
			v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			v.setPadding(_8dp, _8dp, _8dp, _8dp);
			t.setView(v);
			t.setDuration(Toast.LENGTH_LONG);
			internalToast = t;
		}
		((TextView)internalToast.getView()).setText(text);
		internalToast.show();
	}
	
	public static void prepare(Menu menu) {
		final CustomContextMenu mnu = (CustomContextMenu)menu;
		try {
			mnu.setItemClassConstructor(BgButton.class.getConstructor(Context.class));
		} catch (NoSuchMethodException e) {
		}
		mnu.setBackground(new BorderDrawable(color_selected_border, color_menu, true, true, true, true));
		mnu.setPadding(0);
		mnu.setItemTextSizeInPixels(_22sp);
		mnu.setItemTextColor(colorState_text_menu_reactive);
		mnu.setItemGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	}
	
	public static void separator(Menu menu, int groupId, int order) {
		((CustomContextMenu)menu).addSeparator(groupId, order, color_selected_border, strokeSize, _8dp, _2dp, _8dp, _2dp);		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setNextFocusForwardId(View view, int nextFocusForwardId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			view.setNextFocusForwardId(nextFocusForwardId);
	}
	
	public static void prepareDialogAndShow(final AlertDialog dialog) {
		if (alternateTypefaceActive) {
			//https://code.google.com/p/android/issues/detail?id=6360
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				private void scanChildren(ViewGroup parent) {
					for (int i = parent.getChildCount(); i >= 0; i--) {
						final View v = parent.getChildAt(i);
						if (v instanceof ViewGroup)
							scanChildren((ViewGroup)v);
						else if (v instanceof TextView)
							((TextView)v).setTypeface(defaultTypeface);
					}
				}
				
				@Override
				public void onShow(DialogInterface dlg) {
					View v = dialog.findViewById(android.R.id.content);
					if (v != null && (v instanceof ViewGroup)) {
						scanChildren((ViewGroup)v);
					} else {
						//at least try to change the buttons...
						Button btn;
						if ((btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)) != null)
							btn.setTypeface(defaultTypeface);
						if ((btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)) != null)
							btn.setTypeface(defaultTypeface);
						if ((btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)) != null)
							btn.setTypeface(defaultTypeface);
					}
				}
			});
		}
		dialog.show();
	}
}
