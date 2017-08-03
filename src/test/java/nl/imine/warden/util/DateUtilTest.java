package nl.imine.warden.util;

import static org.junit.Assert.assertEquals;

import java.time.Duration;

import org.junit.Test;

public class DateUtilTest {

	@Test
	public void testDurationToString() {
		Duration seconds = Duration.ofSeconds(10);
		assertEquals("10 seconds", DateUtil.durationToString(seconds));

		Duration yearMonthDayHourMinuteSecond = Duration.ofSeconds(34218061);
		assertEquals("1 year, 1 month, 1 day, 1 hour, 1 minute, 1 second", DateUtil.durationToString(yearMonthDayHourMinuteSecond));

		Duration yearsMonthsDaysHoursMinutesSeconds = Duration.ofSeconds(68436122);
		assertEquals("2 years, 2 months, 2 days, 2 hours, 2 minutes, 2 seconds", DateUtil.durationToString(yearsMonthsDaysHoursMinutesSeconds));
	}

	@Test
	public void testDurationFromString() {
		String duration = "2y3M5d1h3m15s";
		assertEquals(Duration.ofSeconds(71283795), DateUtil.fromString(duration));
	}
}
