package com.fantomsoftware.tracker.cmps;

import com.fantomsoftware.tracker.utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler{
//--------------------------------------------------------------------
private final String                  app_path;
private static CustomExceptionHandler instance;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public CustomExceptionHandler( String app_path ){
  this.app_path = app_path;
  instance = this;
}
//--------------------------------------------------------------------
public void uncaughtException( Thread t, Throwable e ){

  String            timestamp   = Utils.GetCurrentTimeStampFile();
  final Writer      result      = new StringWriter();
  final PrintWriter printWriter = new PrintWriter( result );

  e.printStackTrace( printWriter );
  String stacktrace = result.toString();
  printWriter.close();

  String filename = timestamp + ".log";

  if( app_path != null ){
    writeToFile( stacktrace, filename );
  }//if

  Thread.getDefaultUncaughtExceptionHandler().uncaughtException( t, e );
}
//--------------------------------------------------------------------
public static void LogException( Exception e ){

  if(instance == null){
    return;
  }

  String            timestamp   = Utils.GetCurrentTimeStamp();
  final Writer      result      = new StringWriter();
  final PrintWriter printWriter = new PrintWriter( result );

  e.printStackTrace( printWriter );
  String stacktrace = result.toString();
  printWriter.close();

  String filename = timestamp + ".log";

  if( instance.app_path != null ){
    instance.writeToFile( stacktrace, filename );
  }//if

}
//--------------------------------------------------------------------
private void writeToFile( String stacktrace, String filename ){

  try{
    FileWriter     fw  = new FileWriter( app_path + "/" + filename );
    BufferedWriter bos = new BufferedWriter( fw );

    bos.write( stacktrace );
    bos.flush();
    bos.close();

  }catch( Exception e ){
    e.printStackTrace();
  }//try

}
//--------------------------------------------------------------------

}//CustomExceptionHandler