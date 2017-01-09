package com.fantomsoftware.tracker.utils;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;


public class UtilsDate{

  //------------------------------------------------------------------
  public static long DateToLong( GregorianCalendar date ){

    if( date == null ){
      return -1;
    }//if

    return date.getTimeInMillis();

  }//DateToLong
  //------------------------------------------------------------------
  public static GregorianCalendar LongToDate( long date_long ){

    if( date_long == -1 ){
      return null;
    }

    GregorianCalendar date = new GregorianCalendar();
    date.setTimeInMillis( date_long );
    return date;
  }//LongToDate
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public static SimpleDateFormat GetDateFormatter_Full(){
    return new SimpleDateFormat( "MMM-dd HH:mm:ss" );
  }//GetDateFormatter_Full
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public static String LongToDateStringFull( long date_long ){

    SimpleDateFormat formatter = GetDateFormatter_Full();
    GregorianCalendar gc = LongToDate( date_long );

    if( gc == null ){
      return "";
    }//if

    return formatter.format( gc.getTime() );

  }//LongToDateStringFull
  //------------------------------------------------------------------

}//UtilsDate
