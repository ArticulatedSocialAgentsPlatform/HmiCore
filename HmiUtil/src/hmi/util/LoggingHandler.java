/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.util;
import java.util.logging.*;


/**
 * Logging Handler that redirects logging messages to the hmi.util.Console
 * @author Job Zwiers  
 */
public class LoggingHandler extends java.util.logging.Handler {
   
   
   /**
    * Create a new LoggingHandler in java.util.logging style
    */
   public LoggingHandler() {
      super();
   }
   
   /**
    * java.util.logging.Handler method, but has no effect for this Class
    */
   @Override
   public void close() {
   }
   
   /**
    * java.util.logging.Handler method, but has no effect for this Class
    */
   @Override
   public void flush() {
   }
   
   
   /**
    * Prints the LogRecord message on the Console
    */
   @Override
   public void publish(LogRecord rec) {
      Console.println(rec.getMessage());
   }
   
   /**
    * Prints the error on the Console as well as to System.err
    */
   @Override
   public void reportError(String error, Exception ex, int code) {
      System.err.println("LoggingHandler: reportError ");
      Console.println(error + " :" + ex + " code" + code);
      
   }
   
   /*
    * Default Logger
    */
   private static Logger topLevelLogger;
   
   /**
    * showLogging(true)
    */
   public static void showLogging() {
      showLogging(true);
   }
   
   
   /**
    * 
    */
   public static void showLogging(boolean exclusiveMode) {
      topLevelLogger = Logger.getLogger("");
      if (exclusiveMode) {
         Handler[] handlers = topLevelLogger.getHandlers();
         for ( int index = 0; index < handlers.length; index++ ) {
            //System.out.println("Found toplevel handler: " + handlers[index]);
            topLevelLogger.removeHandler(handlers[index]);
            //handlers[index].setLevel( Level.OFF );
         }
      }
      LoggingHandler myh = new LoggingHandler();
      topLevelLogger.addHandler(myh);
   }
 
}
