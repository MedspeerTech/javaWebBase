package com.piotics.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

@Component
public class TimeManager {

	public static ZonedDateTime getCurrentTimestamp() {

		ZonedDateTime currentTime = ZonedDateTime.now();
		ZoneOffset off = currentTime.getOffset();
		currentTime = currentTime.minus(off.getTotalSeconds(), ChronoUnit.SECONDS);
		return currentTime;
	}
}
