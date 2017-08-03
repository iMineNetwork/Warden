package nl.imine.warden.util;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

	public static final int MINUTES_IN_SECONDS = 60;
	public static final int HOURS_IN_SECONDS = MINUTES_IN_SECONDS * 60;
	public static final int DAYS_IN_SECONDS = HOURS_IN_SECONDS * 24;
	public static final int WEEKS_IN_SECONDS = DAYS_IN_SECONDS * 7;
	public static final int MONTHS_IN_SECONDS = DAYS_IN_SECONDS * 30;
	public static final int YEARS_IN_SECONDS = DAYS_IN_SECONDS * 365;

	/**
	 * Converts a String to a date instance. <br>
	 * This function accepts multiple datetype at a time and should match the
	 * following pattern:<br>
	 * <br>
	 * &nbsp;<i>([0-9]+[smhudjy])+</i> (Example: <i>"2w3s1y1w"</i>)<br>
	 * <br>
	 * The number in the string indicates how much of the given time type is to
	 * be<br>
	 * used in creating the date.<br>
	 * Note that the types do not have to be in order for this to work. It is
	 * also possible to have more patterns of the same type it will add them
	 * together rather then overriding. <br>
	 * The character following the number is indicating which type of time type
	 * it should convert to.<br>
	 * Possible time types are: <br>
	 * <br>
	 * &nbsp;<i>'s'</i> for seconds.<br>
	 * &nbsp;<i>'m'</i> for minutes.<br>
	 * &nbsp;<i>'h'</i> or <i>'u'</i> for hours.<br>
	 * &nbsp;<i>'d'</i> for days.<br>
	 * &nbsp;<i>'w'</i> for weeks.<br>
	 * &nbsp;<i>'m'</i> for months.<br>
	 * &nbsp;<i>'y'</i> or <i>'j'</i> for years.<br>
	 * <br>
	 *
	 * @param dateFormat, the String to convert to a Date.
	 * @return an instance of Date created from the String.
	 */
	public static Duration fromString(String dateFormat) {
		Duration duration = Duration.ZERO;
		Matcher m = Pattern.compile("([0-9])+?[A-Za-z]").matcher(dateFormat);
		while (m.find()) {
			duration = duration.plus(fromShortString(m.group()));
		}
		return duration;
	}

	/**
	 * Converts a String to a date instance. <br>
	 * This function only accepts one datetype at a time and should match the
	 * following pattern:<br>
	 * <br>
	 * &nbsp;<i>[0-9]+[smhudjy]</i> (Example: <i>"2w"</i>)<br>
	 * <br>
	 * The number in the string indicates how much of the given time type is to
	 * be<br>
	 * used in creating the date.<br>
	 * <br>
	 * The character following the number is indicating which type of time type
	 * it should convert to.<br>
	 * Possible time types are: <br>
	 * <br>
	 * &nbsp;<i>'s'</i> for seconds.<br>
	 * &nbsp;<i>'m'</i> for minutes.<br>
	 * &nbsp;<i>'h'</i> or <i>'u'</i> for hours.<br>
	 * &nbsp;<i>'d'</i> for days.<br>
	 * &nbsp;<i>'w'</i> for weeks.<br>
	 * &nbsp;<i>'m'</i> for months.<br>
	 * &nbsp;<i>'y'</i> or <i>'j'</i> for years.<br>
	 * <br>
	 * If your string contains more of this pattern use the <i>fromString()</i>
	 * method.
	 *
	 * @param dateFormat, the String to convert to a Date.
	 * @return an instance of Date created from the String.
	 */
	public static Duration fromShortString(String dateFormat) {
		int seconds = 0;
		try {
			char timeChar = dateFormat.charAt(dateFormat.length() - 1);
			int amount = Integer.valueOf(dateFormat.substring(0, dateFormat.length() - 1));
			if (timeChar == 'w') {
				seconds += WEEKS_IN_SECONDS * amount;
			} else {
				seconds += getSecondMultiplier(timeChar) * amount;
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return Duration.ofSeconds(seconds);
	}

	/**
	 * Outputs the a Calendar Time constant from a list of Characters.<br>
	 * Possible time types are: <br>
	 * <br>
	 * &nbsp;<i>'s'</i> for seconds.<br>
	 * &nbsp;<i>'m'</i> for minutes.<br>
	 * &nbsp;<i>'h'</i> or <i>'u'</i> for hours.<br>
	 * &nbsp;<i>'d'</i> for days.<br>
	 * &nbsp;<i>'w'</i> for weeks.<br>
	 * &nbsp;<i>'m'</i> for months.<br>
	 * &nbsp;<i>'y'</i> or <i>'j'</i> for years.<br>
	 * <br>
	 *
	 * @param flag the flag you want to convert to the Calendar time type
	 *             constant.
	 * @return an integer representing the time type constant as defined in the
	 * Calendar class.
	 */
	public static int getSecondMultiplier(char flag) {
		switch (flag) {
			case 's':
			case 'S':
				return 1;
			case 'm':
				return MINUTES_IN_SECONDS;
			case 'h':
			case 'H':
			case 'u':
			case 'U':
			default:
				return HOURS_IN_SECONDS;
			case 'd':
			case 'D':
				return DAYS_IN_SECONDS;
			case 'w':
			case 'W':
				return WEEKS_IN_SECONDS;
			case 'M':
				return MONTHS_IN_SECONDS;
			case 'j':
			case 'J':
			case 'y':
			case 'Y':
				return YEARS_IN_SECONDS;
		}
	}

	public static String durationToString(Duration duration) {
		long secondsBetween = duration.getSeconds();
		String ret = "";
		double minus = 0;
		if (secondsBetween >= YEARS_IN_SECONDS) {
			minus = Math.floor(secondsBetween / YEARS_IN_SECONDS);
			secondsBetween -= (long) minus * YEARS_IN_SECONDS;
			ret += (int) minus + " year" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween >= MONTHS_IN_SECONDS) {
			minus = Math.floor(secondsBetween / MONTHS_IN_SECONDS);
			secondsBetween -= (long) minus * MONTHS_IN_SECONDS;
			ret += (int) minus + " month" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween >= WEEKS_IN_SECONDS) {
			minus = Math.floor(secondsBetween / WEEKS_IN_SECONDS);
			secondsBetween -= (long) minus * WEEKS_IN_SECONDS;
			ret += (int) minus + " week" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween >= DAYS_IN_SECONDS) {
			minus = Math.floor(secondsBetween / DAYS_IN_SECONDS);
			secondsBetween -= (long) minus * DAYS_IN_SECONDS;
			ret += (int) minus + " day" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween >= HOURS_IN_SECONDS) {
			minus = Math.floor(secondsBetween / HOURS_IN_SECONDS);
			secondsBetween -= (long) minus * HOURS_IN_SECONDS;
			ret += (int) minus + " hour" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween >= MINUTES_IN_SECONDS) {
			minus = Math.floor(secondsBetween / MINUTES_IN_SECONDS);
			secondsBetween -= (long) minus * MINUTES_IN_SECONDS;
			ret += (int) minus + " minute" + (minus == 1 ? "" : "s") + ", ";
		}
		if (secondsBetween > 0) {
			ret += secondsBetween + " second" + (secondsBetween == 1 ? "" : "s");
		}
		if (ret.endsWith(", ")) {
			ret = ret.substring(0, ret.length() - 2);
		}
		return ret;
	}
}
