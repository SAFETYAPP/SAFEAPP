package com.viewnine.safeapp.ulti;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
	private static final int HOURS_IN_DAY = 24;
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	public static final long ONE_HOUR = 60 * 60 * 1000;

	public static final String RFC_USA_1 = "MMM dd, yyyy hh:mm a";// Feb 27,
																	// 2012
																	// 05:40 PM
	//public static final String RFC_USA_2 = "dd/M/yyyy HH:mm a";// 28/3/2012
																// 14:17 PM
//	public static final String RFC_USA_2 = "MM/dd/yyyy HH:mm a";// 28/3/2012
	public static final String RFC_USA_2 = "MM/dd/yyyy hh:mm a";// 28/3/2012
	// 14:17 PMO
	public static final String RFC_USA_3 = "MMMMMMMMMMMM dd, yyyy"; // June 28,
																	// 1999
	public static final String RFC_USA_4 = "yyyy-M-dd HH:mm"; // June 28, 1999
	public static final String RFC_USA_5 = "MMM dd, yyyy hh:mm a";// Feb 27,
																	// 2012
																	// 17:40
	public static final String RFC_USA_6 = "yyyy-MM-dd";
	public static final String RFC_USA_7 = "MMM dd, yyyy - hh:mm a";

	public static final String RFC_USA_8 = "yyyy-MM-dd hh:mm";// 2012-06-13
																// 20:35

	public static final String RFC_USA_9 = "hh:mm a";// 14:17 pm
	
	public static final String RFC_USA_10 = "yyyy-MM-dd HH:mm:ss";// 2012-06-13 20:35:15
	
	public static final String RFC_USA_11 = "dd/MM/yyyy"; //20/10/2013
	
	public static final String XMPP_UTC_1 = "yyyyMMdd'T'HH:mm:ss";
	public static final String XMPP_UTC_2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static final String RFC_NOTIFICATION = "MM/dd/yy";// 09/13/12
	public static final String RFC_GAME_RESULT = "MM/dd/yyyy";// 09/13/2012
	public static final String RFC_CHECKIN = "MM/dd/yy";// 09/13/12

	public static final String RFC_YESTERDAY = "hh:mm";// 09/13/12
	// 20:35

	public static final String[] MONTH = { "Jan", "Feb", "Mar", "Apr", "May",
			"June", "July", "Aug", "Sept", "Oct", "Nov", "Dec", };
	public static final String[] FULLMONTH = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December", };
	public static final String[] DAY = { "Sun", "Mon", "Tue", "Wed", "Thu",
			"Fri", "Sat", };

	public static int YESTERDAY = -1;
	public static int PAST = -2;
	public static int TODAY = 0;
//	private static SimpleDateFormat LocalTimeFormatter = new SimpleDateFormat(
//			"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	private static SimpleDateFormat GMTTimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");	

	public DateHelper() {
		GMTTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String getDateForWheel(Calendar cal) {
		Date date = cal.getTime();
		String s = DAY[date.getDay()] + " " + MONTH[date.getMonth()] + " "
				+ date.getDate();
		return s;
	}

	public static String getDateForWeb(Calendar calendar) {
		// "2011-01-01"
		final String rs = String.format("%d-%d-%d",
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,// -1
																				// be
																				// count
																				// calendar
																				// 1-12
				calendar.get(Calendar.DAY_OF_MONTH));
		return rs;
	}

	public static String getDateShort(Calendar calendar) {
		// "1/10/2012" month, day, year
		final String rs = String.format("%d/%d/%d",
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR));
		return rs;
	}

	public static String getDateShortForGame(Calendar calendar) {
		// "1/10/2012"
		final String rs = String.format("%d/%d/%d",
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR));
		return rs;
	}

	public static String getDateTimeForWeb(Calendar cal) {

		// 2011-12-31 23:59:59
		final String rs = cal.get(Calendar.YEAR) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-"
				+ cal.get(Calendar.DAY_OF_MONTH) + "%20"
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":00";
		return rs;
	}

	public static String getDateForPosting(Calendar cal) {

		// 2011-12-31 23:59:59
//		final String rs = cal.get(Calendar.YEAR) + "-"
//				+ (cal.get(Calendar.MONTH) + 1) + "-"
//				+ cal.get(Calendar.DAY_OF_MONTH) + " "
//				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
////				+ cal.get(Calendar.MINUTE) + ":00";
//				+ cal.get(Calendar.MINUTE) + ":"
//				+ cal.get(Calendar.SECOND);
		
		final String rs = getCurrentDate2(cal);
		
		return rs;
	}

	public static int compareDate(Calendar cal) {
		Calendar now = Calendar.getInstance();

		int rs = cal.getTime().getDate() - now.getTime().getDate();
		int remainMonth = cal.getTime().getMonth() - now.getTime().getMonth();
		int remainYear = cal.getTime().getYear() - now.getTime().getYear();

		if (remainYear != 0) {
			return PAST;
		}

		if (remainMonth != 0) {
			return PAST;
		}

		if (rs == TODAY) {
			return TODAY;
		}

		if (rs == YESTERDAY) {
			return YESTERDAY;
		}

		return PAST;
	}


	public static String getTimeContactCheckin(Calendar cal) {
		return parse(cal.getTime(), RFC_CHECKIN);
	}

	public static String getTimeGameResult(Calendar cal) {
		return parse(cal.getTime(), RFC_GAME_RESULT);
	}

	public static String getTimeAnomationNotification(Calendar cal) {
		return getTimePastInMinute(cal);
		// return parse(cal.getTime(), RFC_NOTIFICATION);
	}

	public static String getTimeNotificationMessage(Calendar cal) {
		Calendar now = Calendar.getInstance();

		long mili = now.getTimeInMillis() - cal.getTimeInMillis();
		long totalMinute = (mili / 1000) / 60;
		long day = (totalMinute / 60) / HOURS_IN_DAY;

		if (day <= 0) {
			return parse(cal.getTime(), "HH:mm");
		}
		return parse(cal.getTime(), "dd MMM");
	}

	public static String getDateFormat(Calendar cal, String format) {
		Date date = cal.getTime();
		String rs = parse(date, format);
		return rs;
	}

	public static String parse(Date date, String format) {
		SimpleDateFormat dateParser = new SimpleDateFormat(format);
		dateParser.setTimeZone(TimeZone.getDefault());

		return dateParser.format(date);
	}

	public static Date parse(String s, String format) {
		SimpleDateFormat dateParser = new SimpleDateFormat(format);
		dateParser.setTimeZone(TimeZone.getDefault());
		Date date = new Date();
		try {
			date = dateParser.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getDateForUpdateList(Calendar cal) {
		String am = "a.m.";
		if (cal.get(Calendar.AM_PM) > 0) {
			am = "p.m.";
		}
		// 31/12/2011 23:59 a.m.
		final String rs = cal.get(Calendar.DAY_OF_MONTH) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR)
				+ " "
				+ cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + " " + am;
		return rs;
	}

	// for chat time
	public static String getTime(Calendar cal) {

		String am = "am";
		if (cal.get(Calendar.AM_PM) > 0) {
			am = "pm";
		}
		// 1:40 PM
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		String timeString;

		if (hour > 12)
			hour = hour - 12;

		if (minute < 10)
			timeString = String.format("%s:0%s %s", String.valueOf(hour),
					String.valueOf(minute), am);
		else
			timeString = String.format("%s:%s %s", String.valueOf(hour),
					String.valueOf(minute), am);

		return timeString;
	}

	// for UI
	public static String getFullDateUI(Calendar cal) {

		String am = "AM";
		if (cal.get(Calendar.AM_PM) > 0) {
			am = "PM";
		}
		// mon, aug 8, 2011 1:40 PM
		Date date = cal.getTime();
		final String s = String.format("%s, %s %s, %s  %s:%s %s",
				DAY[date.getDay()], MONTH[date.getMonth()],
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(cal.getTime().getYear() + 1900),
				String.valueOf(cal.get(Calendar.HOUR)),
				String.valueOf(cal.get(Calendar.MINUTE)), am);
		return s;
	}

	public static String getCurrentDate(Calendar cal) {

		String am = "AM";
		if (cal.get(Calendar.AM_PM) > 0) {
			am = "PM";
		}
		// mon, aug 8, 2011 1:40 PM
		Date date = cal.getTime();
		final String s = String.format("%s %s, %s %s:%s %s",
				MONTH[date.getMonth()],
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(cal.getTime().getYear() + 1900),
				String.valueOf(cal.get(Calendar.HOUR)),
				String.valueOf(cal.get(Calendar.MINUTE)), am);
		return s;
	}
	
	/**
	 * "yyyy-MM-dd HH:mm:ss";
	 * @param cal
	 * @return
	 */
	public static String getCurrentDate2(Calendar cal) {
		SimpleDateFormat customFormat = new SimpleDateFormat(RFC_USA_10); 		
		return customFormat.format(cal.getTime());
	}
	
	public static String milliToDateString(long milliseconds) {
		SimpleDateFormat customFormat = new SimpleDateFormat(RFC_USA_10); 		
		return customFormat.format(milliseconds);
	}

	public static String getDateMessage(Calendar cal) {
		// Dec 20, 1988
		final String s = String.format("%s %s, %s",
				MONTH[cal.get(Calendar.MONTH)],
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(cal.getTime().getYear() + 1900));
		return s;
	}

	public static String getDateMessageFullMonth(Calendar cal) {
		// December 20, 1988
		final String s = String.format("%s %s, %s",
				FULLMONTH[cal.get(Calendar.MONTH)],
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(cal.getTime().getYear() + 1900));
		return s;
	}

	public String getFullDate() {
		return getFullDateUI(Calendar.getInstance());
	}

	public static String getCurrentDateTime() {
		final Calendar today = Calendar.getInstance();
		final String rs = today.getTime().toString();
		return rs;
	}

	public static String getCurrentDateTimeChat() {
		final Calendar today = Calendar.getInstance();
		Date date = today.getTime();
		final StringBuffer rs = new StringBuffer();
		rs.append((date.getYear() + 1900));
		rs.append("-");
		rs.append(date.getMonth() + 1);// because date.getMonth() return month
										// number 0 - 11
		rs.append("-");
		rs.append(date.getDate());
		rs.append(" ");
		rs.append(date.getHours());
		rs.append(":");
		rs.append(date.getMinutes());
		rs.append(":");
		rs.append(date.getSeconds());
		return rs.toString();
	}

	// 2011-11-29 07:28:26
	public static Calendar stringToCalendar(String stringDate) {
		LogUtils.logI("ttnlan", "stringToCalendar stringDate="+stringDate);
		if (stringDate == null || "null".equalsIgnoreCase(stringDate)) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		form.setTimeZone(TimeZone.getDefault());
		try {
			date = form.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			calendar.setTime(date);
			return calendar;
		}
		return null;
	}	
	
	public static String calendarToString(Calendar calendar, String typeFormat){
		SimpleDateFormat dateFormat = new SimpleDateFormat(typeFormat);
		return dateFormat.format(calendar.getTime());
	}

	// 2011-11-29 07:28:26
	public static Calendar stringToCalendar(String stringDate, String type) {
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		SimpleDateFormat form = new SimpleDateFormat(type);
		form.setTimeZone(TimeZone.getDefault());
		try {
			date = form.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar stringDateToCalendar(String stringDate) {
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = form.parse(stringDate);
			calendar.setTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendar;
	}

	public static Calendar getCalendar(String stringDate) {
		if(TextUtils.isEmpty(stringDate)){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		Date date = null;
		SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy");
		try {
			date = form.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar convertGMTtoLocalFromString(String stringDate) {
		Calendar calendarGMT = stringToCalendar(stringDate);
		return convertGMTtoLocal(calendarGMT);
	}
	
	public static Calendar convertUTCtoLocalFromString(String stringDate) {
		Calendar calendarUTC = stringToCalendar(stringDate);
		return convertUTCtoLocal(calendarUTC);
	}

	public static boolean isInDayLight() {
		Date date = Calendar.getInstance().getTime();
		TimeZone tz = TimeZone.getDefault();
		return tz.inDaylightTime(date);
	}

	public static Calendar convertGMTtoLocal(Calendar calendarGMT) {
		if (calendarGMT == null)
			return null;
		
		Calendar calendarLocal = (Calendar) calendarGMT.clone();
		TimeZone z = TimeZone.getDefault();
		int offset = z.getRawOffset();
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		// Thien add to fix bug Daylight saving time
		if (isInDayLight()) {
			Log.i("daylight", "in Daylight saving time");
			offsetHrs++;
		}

		calendarLocal.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarLocal.add(Calendar.MINUTE, (offsetMins));

		return calendarLocal;
	}
	
	public static Calendar convertUTCtoLocal(Calendar calendarUTC) {
		if (calendarUTC == null)
			return null;
		
		Calendar calendarLocal = (Calendar) calendarUTC.clone();
		TimeZone z = TimeZone.getDefault();
		int offset = z.getRawOffset();
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		// Thien add to fix bug Daylight saving time
		if (isInDayLight()) {
			Log.i("daylight", "in Daylight saving time");
			offsetHrs++;
		}

		calendarLocal.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarLocal.add(Calendar.MINUTE, (offsetMins));

		return calendarLocal;
	}
	
	public static String convertUTCtoLocal(String calendarUTCTMP) {
		Calendar calendarUTC = stringToCalendar(calendarUTCTMP);
		
		Calendar calendarLocal = (Calendar) calendarUTC.clone();
		TimeZone z = TimeZone.getDefault();
		int offset = z.getRawOffset();
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		// Thien add to fix bug Daylight saving time
		if (isInDayLight()) {
			Log.i("daylight", "in Daylight saving time");
			offsetHrs++;
		}

		calendarLocal.add(Calendar.HOUR_OF_DAY, (offsetHrs));
		calendarLocal.add(Calendar.MINUTE, (offsetMins));
		SimpleDateFormat dateFormat = new SimpleDateFormat(RFC_USA_10);
		return dateFormat.format(calendarLocal.getTime());
	}
	
	public static String convertLocalToGMT(Calendar calendar){
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(RFC_USA_10);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Date date = calendar.getTime();
		
//		LogUtils.logI(TAG , "Convert " + localDate + " to GMT: " + dateFormatGmt.format(date));
		return dateFormatGmt.format(date);
		
	}
	
	public static String convertLocalToUTC(Calendar calendar, String typeFormat){
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(typeFormat);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		Date date = calendar.getTime();
		return dateFormatGmt.format(date);
		
	}
	
	public static String convertCalendarToString(Calendar calendar, String typeFormat){
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(typeFormat);
		Date date = calendar.getTime();
		return dateFormatGmt.format(date);
	}

	public static String getTimePastInMinute(Calendar cal) {

		if (cal == null) {
			return "";
		}
		Calendar now = Calendar.getInstance();

		long mili = now.getTimeInMillis() - cal.getTimeInMillis();

		long totalMinute = (mili / 1000) / 60;
		long minute = totalMinute % 60;
		long hour = (totalMinute / 60) % 24;
		long day = (totalMinute / 60) / 24;

		StringBuilder rs = new StringBuilder();

		if (day == 1) {
			return "yesterday " + parse(cal.getTime(), RFC_YESTERDAY);
		}

		if (day > 1) {
			rs.append(day + " days");
			rs.append(" ago");
			return rs.toString();
		}
		if (hour > 0) {
			if(hour > 1){
				rs.append(" " + hour + " hours");
			}else {
				rs.append(" " + hour + " hour");
			}
			rs.append(" ago");
			return rs.toString();
		}

		if (minute <= 0) {
			minute = 1;
		}

		if (minute > 0) {
			if(minute > 1){
				rs.append(" " + minute + " minutes");
			}else {
				rs.append(" " + minute + " minute");
			}
			rs.append(" ago");
			return rs.toString();
		}

		// return minute + " minute ago";
		return rs.toString();
	}

	public static boolean isOverTwoHour(String s) {
		try {
			if (Calendar.getInstance().getTimeInMillis()
					- stringToCalendar(s, RFC_USA_5).getTimeInMillis() > ONE_HOUR * 2) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}



	public static boolean isThisDateValid(String dateToValidate, String dateFromat) {

		if (dateToValidate == null || dateToValidate.equals(Constants.ZERODAY)
				|| TextUtils.isEmpty(dateToValidate)) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);

		try {
			// if not valid, it will throw ParseException
			sdf.parse(dateToValidate);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public static int convertLongToHour(long miliseconds){
		long tmp = miliseconds / (1000 * 60 * 60);
//		int result = (int) miliseconds / (1000 * 60 * 60);
		return (int) tmp;
	}
	public static int convertLongToMinute(long miliseconds){
		return (int) miliseconds / (1000 * 60);
	}
	public static int convertLongToSecond(long miliseconds){
		return (int) miliseconds / 1000;
	}
	public static int convertLongToDays(long miliseconds){
		long result = miliseconds / (24 * 60 * 60 * 1000);
		return (int)result;
	}
	
	public static String getCurrentGMTTime(){
		Date currentTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(RFC_USA_10);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return dateFormat.format(currentTime);
	}
	
	public static String convertGMTStringToUTCString(String GMTString){
		String localTime = calendarToString(convertGMTtoLocalFromString(GMTString), RFC_USA_10);
		String UTCTime = convertLocalToUTC(stringToCalendar(localTime), DateHelper.RFC_USA_10);
		return UTCTime;
	}
	
	public static String convertDateStringBaseOnFormat(String formatFrom, String formatTo, String date){
		if(TextUtils.isEmpty(date)) return Constants.EMPTY_STRING;
		SimpleDateFormat dateFormatFrom = new SimpleDateFormat(formatFrom);
		SimpleDateFormat dateFormatTo = new SimpleDateFormat(formatTo);
		try {
			Date dateTmp = dateFormatFrom.parse(date);
			return dateFormatTo.format(dateTmp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Constants.EMPTY_STRING;
		}
	}
	
	public static Date convertStringToDate(String strDate){
		SimpleDateFormat formatter = new SimpleDateFormat(RFC_USA_6);
		Date date = null;
		try {
	 
			date = formatter.parse(strDate);
	 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static Calendar convertStringToCalendar(String strDate, String formarDate){
		SimpleDateFormat formatter = new SimpleDateFormat(RFC_USA_6);
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		try {
	 
			date = formatter.parse(strDate);
			calendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar;
	}
	
	public static String getDateMessageFullMonth(Date date) {
		Calendar cal = DateToCalendar(date);
		// December 20, 1988
		final String s = String.format("%s %s, %s",
				FULLMONTH[cal.get(Calendar.MONTH)],
				String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
				String.valueOf(cal.getTime().getYear() + 1900));
		return s;
	}
	
	public static Calendar DateToCalendar(Date date){ 
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
	}
	
	public static boolean compareEqualDates(String dateStr1, String dateStr2, String formatDate){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
		try {
			Date date1 = simpleDateFormat.parse(dateStr1);
			Date date2 = simpleDateFormat.parse(dateStr2);
			if(date1.compareTo(date2) == 0){
				return true;
			}else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static String convertLongMilisecondsToDateString(long dateLong, String formatDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dateLong);
		Date date = (Date) calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(formatDate);
		String time = format.format(date);
		return time;
	}
	
}
