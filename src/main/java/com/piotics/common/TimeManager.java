package com.piotics.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

@Component
public class TimeManager {

    public static ZonedDateTime getCurrentTimestamp(){//      final Date currentTime = new Date();
//
//      final SimpleDateFormat sdf =
//              new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
//
//      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//      System.out.println("GMT time: " + sdf.format(currentTime));

      ZonedDateTime currentTime = ZonedDateTime.now();
      ZoneOffset off=currentTime.getOffset();
      currentTime=currentTime.minus(off.getTotalSeconds(), ChronoUnit.SECONDS);

      return currentTime;
    }

}
