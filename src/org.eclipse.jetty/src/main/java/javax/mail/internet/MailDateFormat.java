/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * @(#)MailDateFormat.java	1.17	07/05/04
 */

package javax.mail.internet;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.ParseException;

/**
 * Formats and parses date specification based on the
 * draft-ietf-drums-msg-fmt-08 dated January 26, 2000. This is a followup
 * spec to RFC822.<p>
 *
 * This class does not take pattern strings. It always formats the
 * date based on the specification below.<p>
 *
 * 3.3 Date and Time Specification<p>
 *
 * Date and time occur in several header fields of a message. This section
 * specifies the syntax for a full date and time specification. Though folding
 * whitespace is permitted throughout the date-time specification, it is
 * recommended that only a single space be used where FWS is required and no
 * space be used where FWS is optional in the date-time specification; some
 * older implementations may not interpret other occurrences of folding
 * whitespace correctly.<p>
 *
 * date-time = [ day-of-week "," ] date FWS time [CFWS]<p>
 *
 * day-of-week = ([FWS] day-name) / obs-day-of-week<p>
 *
 * day-name = "Mon" / "Tue" / "Wed" / "Thu" / "Fri" / "Sat" / "Sun"<p>
 *
 * date = day month year<p>
 *
 * year = 4*DIGIT / obs-year<p>
 *
 * month = (FWS month-name FWS) / obs-month<p>
 *
 *<pre>month-name = "Jan" / "Feb" / "Mar" / "Apr" /
 *             "May" / "Jun" / "Jul" / "Aug" /
 *             "Sep" / "Oct" / "Nov" / "Dec"
 * </pre><p>
 * day = ([FWS] 1*2DIGIT) / obs-day<p>
 *
 * time = time-of-day FWS zone<p>
 *
 * time-of-day = hour ":" minute [ ":" second ]<p>
 *
 * hour = 2DIGIT / obs-hour<p>
 *
 * minute = 2DIGIT / obs-minute<p>
 *
 * second = 2DIGIT / obs-second<p>
 *
 * zone = (( "+" / "-" ) 4DIGIT) / obs-zone<p>
 *
 *
 * The day is the numeric day of the month. The year is any numeric year in
 * the common era.<p>
 *
 * The time-of-day specifies the number of hours, minutes, and optionally
 * seconds since midnight of the date indicated.<p>
 *
 * The date and time-of-day SHOULD express local time.<p>
 *
 * The zone specifies the offset from Coordinated Universal Time (UTC,
 * formerly referred to as "Greenwich Mean Time") that the date and
 * time-of-day represent. The "+" or "-" indicates whether the time-of-day is
 * ahead of or behind Universal Time. The first two digits indicate the number
 * of hours difference from Universal Time, and the last two digits indicate
 * the number of minutes difference from Universal Time. (Hence, +hhmm means
 * +(hh * 60 + mm) minutes, and -hhmm means -(hh * 60 + mm) minutes). The form
 * "+0000" SHOULD be used to indicate a time zone at Universal Time. Though
 * "-0000" also indicates Universal Time, it is used to indicate that the time
 * was generated on a system that may be in a local time zone other than
 * Universal Time.<p>
 *
 * A date-time specification MUST be semantically valid. That is, the
 * day-of-the week (if included) MUST be the day implied by the date, the
 * numeric day-of-month MUST be between 1 and the number of days allowed for
 * the specified month (in the specified year), the time-of-day MUST be in the
 * range 00:00:00 through 23:59:60 (the number of seconds allowing for a leap
 * second; see [STD-12]), and the zone MUST be within the range -9959 through
 * +9959.<p>
 *
 * @author Max Spivak
 * @since		JavaMail 1.2
 */

public class MailDateFormat extends SimpleDateFormat {

    private static final long serialVersionUID = -8148227605210628779L;

    public MailDateFormat() {
	super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
    }

    /**
     * Formats the given date in the format specified by 
     * draft-ietf-drums-msg-fmt-08 in the current TimeZone.
     *
     * @param   date            the Date object
     * @param   dateStrBuf      the formatted string
     * @param   fieldPosition   the current field position
     * @return	StringBuffer    the formatted String
     * @since			JavaMail 1.2
     */
    public StringBuffer format(Date date, StringBuffer dateStrBuf,
			       FieldPosition fieldPosition) {

	/* How this method works: First format the date with the
	 * format specified in the constructor inserting string 'XXXXX' 
	 * where the timezone offset goes. Find where in the string the
	 * string 'XXXXX' appears and remember that in var "pos". 
	 * Calculate the offset, taking the DST into account and insert
	 * it into the stringbuffer at position pos.
	 */

	int start = dateStrBuf.length();
	super.format(date, dateStrBuf, fieldPosition);
	int pos = 0;
	// find the beginning of the 'XXXXX' string in the formatted date
	// 25 is the first position that we expect to find XXXXX at
	for (pos = start + 25; dateStrBuf.charAt(pos) != 'X'; pos++)
	    ;

	// set the timezone to +HHMM or -HHMM
	calendar.clear();
	calendar.setTime(date);
	int offset = calendar.get(Calendar.ZONE_OFFSET) +
			calendar.get(Calendar.DST_OFFSET);
	// take care of the sign
	if (offset < 0) {
	    dateStrBuf.setCharAt(pos++, '-');
	    offset = (-offset);
	} else
	    dateStrBuf.setCharAt(pos++, '+');

	int rawOffsetInMins = offset / 60 / 1000; // offset from GMT in mins
 	int offsetInHrs = rawOffsetInMins / 60;
	int offsetInMins = rawOffsetInMins % 60;

	dateStrBuf.setCharAt(pos++, Character.forDigit((offsetInHrs/10), 10));
	dateStrBuf.setCharAt(pos++, Character.forDigit((offsetInHrs%10), 10));
	dateStrBuf.setCharAt(pos++, Character.forDigit((offsetInMins/10), 10));
	dateStrBuf.setCharAt(pos++, Character.forDigit((offsetInMins%10), 10));
	// done with timezone
	
	return dateStrBuf;
    }

    ////////////////////////////////////////////////////////////

    /**
     * Parses the given date in the format specified by
     * draft-ietf-drums-msg-fmt-08 in the current TimeZone.
     *
     * @param   text    the formatted date to be parsed
     * @param   pos     the current parse position
     * @return	Date    the parsed date in a Date object
     * @since		JavaMail 1.2
     */
    public Date parse(String text, ParsePosition pos) {
	return parseDate(text.toCharArray(), pos, isLenient());
    }

    /*
      Valid Examples:
      
      Date: Sun, 21 Mar 1993 23:56:48 -0800 (PST)
      Date: 
      Date: Mon, 22 Mar 1993 09:41:09 -0800 (PST)
      Date:     26 Aug 76 14:29 EDT

      */
    
    /**
     * method of what to look for:
     *
     *
     *	skip WS
     *	skip day ","  	(this is "Mon", "Tue")
     *	skip WS
     *
     *	parse number (until WS) ==> 1*2DIGIT (day of month)
     *
     *	skip WS
     *
     *	parse alpha chars (until WS) ==> find month
     *
     *	skip WS
     *
     *	parse number (until WS) ==> 2*4DIGIT (year)
     *
     *	skip WS
     *
     *	// now looking for time
     *	parse number (until ':') ==> hours
     *	parse number (until ':') ==> minutes
     *	parse number (until WS) ==> seconds
     *
     *	// now look for Time Zone
     *	skip WS
     *	if ('+' or '-') then numerical time zone offset
     *  if (alpha) then alpha time zone offset
     */

    static boolean debug = false;

    /**
     * create a Date by parsing the char array
     */
    static private Date parseDate(char[] orig, ParsePosition pos,
				boolean lenient) {
	try {
	    int day = -1;
	    int month = -1;
	    int year = -1;
	    int hours = 0;
	    int minutes = 0;
	    int seconds = 0;
	    int offset = 0;
		
			
	    MailDateParser p = new MailDateParser(orig);
			
	    // get the day
	    p.skipUntilNumber();
	    day = p.parseNumber();

	    if (!p.skipIfChar('-')) {  // for IMAP internal Date
		p.skipWhiteSpace();
	    }

	    // get the month		
	    month = p.parseMonth();
	    if (!p.skipIfChar('-')) {  // for IMAP internal Date
		p.skipWhiteSpace();
	    }
	    
	    // get the year
	    year = p.parseNumber();	// should not return a negative number
	    if (year < 50) {
		year += 2000;
	    } else if (year < 100) {
		year += 1900;
	    } // otherwise the year is correct (and should be 4 digits)
			
			
	    // get the time
	    // first get hours
	    p.skipWhiteSpace();
	    hours = p.parseNumber();
			
	    // get minutes
	    p.skipChar(':');
	    minutes = p.parseNumber();
	    
	    // get seconds  (may be no seconds)
	    if (p.skipIfChar(':')) {
		seconds = p.parseNumber();
	    }
	    
			
	    // try to get a Time Zone
	    try {
		p.skipWhiteSpace();
		offset = p.parseTimeZone();
	    } catch (ParseException pe) {
		if (debug) {
		    System.out.println("No timezone? : '" +
						new String(orig) + "'");
		}
	    }
			
	    pos.setIndex(p.getIndex());
	    Date d = ourUTC(year, month, day, hours, minutes, seconds, offset,
							lenient);
	    return d;

	} catch (Exception e) {
	    // Catch *all* exceptions, including RuntimeExceptions like
	    // ArrayIndexOutofBoundsException ... we need to be
	    // extra tolerant of all those bogus dates that might screw
	    // up our parser. Sigh.

	    if (debug) {
		System.out.println("Bad date: '" + new String(orig) + "'");
		e.printStackTrace();
	    }
	    pos.setIndex(1); // to prevent DateFormat.parse() from throwing ex
	    return null;
	}
    }
	
    private static TimeZone tz = TimeZone.getTimeZone("GMT");
    private static Calendar cal = new GregorianCalendar(tz);
    private synchronized static Date ourUTC(int year, int mon, int mday,
					   int hour, int min, int sec,
					   int tzoffset, boolean lenient) {
	// clear the time and then set all the values
	cal.clear();
	cal.setLenient(lenient);
	cal.set(Calendar.YEAR, year);
	cal.set(Calendar.MONTH, mon);
	cal.set(Calendar.DATE, mday);
	cal.set(Calendar.HOUR_OF_DAY, hour);
	cal.set(Calendar.MINUTE, min + tzoffset);  // adjusted for the timezone
	cal.set(Calendar.SECOND, sec);

	return cal.getTime();
    }	


    ////////////////////////////////////////////////////////////
    
    /** Don't allow setting the calendar */
    public void setCalendar(Calendar newCalendar) {
	throw new RuntimeException("Method setCalendar() shouldn't be called");
    }

    /** Don't allow setting the NumberFormat */
    public void setNumberFormat(NumberFormat newNumberFormat) {
	throw new RuntimeException("Method setNumberFormat() shouldn't be called");
    }
    

    /* test code for MailDateFormat */
    /*
    public static void main(String[] args) {
	DateFormat df = new MailDateFormat();
	Date d = new Date();

	// test output in all the timezones
	System.out.println("------- test all timezones  ---------------");
	System.out.println("Current date: " + d);
	String[] allIDs = TimeZone.getAvailableIDs();
	for (int i = 0; i < allIDs.length; i++) {
	    TimeZone tz = TimeZone.getTimeZone(allIDs[i]);
	    df.setTimeZone(tz);
	    System.out.println("Date in " + tz.getID() + ":    " +
			       df.format(new Date()));
	}
	try {
        System.out.println(df.parse("Sun, 21 Mar 1993 23:56:48 -0800 (PST)"));
	System.out.println(df.parse("Mon, 22 Mar 1994 13:34:51 +0000"));
	System.out.println(df.parse("26 Aug 76 14:29 EDT"));
	System.out.println(df.parse("15 Apr 11 23:49 EST"));
	System.out.println(df.parse("15 Apr 11 23:49 ABC"));
	} catch (ParseException pex) {
	    pex.printStackTrace();
	}

	// reset DateFormat TZ
	df.setTimeZone(TimeZone.getDefault());

	// test all days in a month
	System.out.println();
	System.out.println("------- test all days in a month ---------------");
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, 1972);
	cal.set(Calendar.MONTH, Calendar.OCTOBER);
	cal.set(Calendar.DATE, 1);
	cal.set(Calendar.HOUR, 10);
	cal.set(Calendar.MINUTE, 50);
	cal.set(Calendar.AM_PM, Calendar.PM);
	System.out.println("Initial Date: " + cal.getTime());
	System.out.println("Current Date: " + df.format(cal.getTime()));
	for (int i = 0; i < 30; i++) {
	    cal.roll(Calendar.DATE, true);
	    System.out.println("Current Date: " + df.format(cal.getTime()));
	}

	// test all months
	System.out.println();
	System.out.println("------- test all months in a year -----------");
	cal.set(Calendar.MONTH, Calendar.JANUARY);
	cal.set(Calendar.DATE, 7);
	System.out.println("Initial Date: " + cal.getTime());
	System.out.println("Current Date: " + df.format(cal.getTime()));
	for (int i = 1; i < 12; i++) {
	    cal.roll(Calendar.MONTH, true);
	    System.out.println("Current Date: " + df.format(cal.getTime()));
	}

	// test leap years
	System.out.println();
	System.out.println("------- test leap years -----------");
	cal.set(Calendar.YEAR, 1999);
	cal.set(Calendar.MONTH, Calendar.JANUARY);
	cal.set(Calendar.DATE, 31);
	cal.roll(Calendar.MONTH, true);
	System.out.println("Initial Date: " + cal.getTime());
	System.out.println("Current Date: " + df.format(cal.getTime()));
	for (int i = 1; i < 12; i++) {
	    cal.set(Calendar.MONTH, Calendar.JANUARY);
	    cal.set(Calendar.DATE, 31);
	    cal.roll(Calendar.YEAR, true);
	    cal.roll(Calendar.MONTH, true);
	    System.out.println("Current Date: " + df.format(cal.getTime()));
	}
    }
    */

}

/**
 * Helper class to deal with parsing the characters
 */
class MailDateParser {
    
    int index = 0;
    char[] orig = null;

    public MailDateParser(char[] orig) {
	this.orig = orig;
    }

    /**
     * skips chars until it finds a number (0-9)
     *
     * if it does not find a number, it will throw
     * an ArrayIndexOutOfBoundsException
     */	
    public void skipUntilNumber() throws ParseException {
	try {
	    while (true) {
		switch ( orig[index] ) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		    return;
				
		default:
		    index++;
		    break;	
		}
	    }
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new ParseException("No Number Found", index);
	}
    }

    /**
     * skips any number of tabs, spaces, CR, and LF - folding whitespace
     */
    public void skipWhiteSpace() {
	int len = orig.length;
	while (index < len) {
	    switch (orig[index]) {
	    case ' ': // space
	    case '\t': // tab
	    case '\r': // CR
	    case '\n': // LF
		index++;
		break;
					
	    default:
		return;
	    }	
	}
    }


    /**
     * used to look at the next character without "parsing" that
     * character.
     */
    public int peekChar() throws ParseException {
	if (index < orig.length)
	    return orig[index];
	else
	    throw new ParseException("No more characters", index);
    }
	
    /**
     * skips the given character.  if the current char does not
     * match a ParseException will be thrown
     */
    public void skipChar(char c) throws ParseException {
	if (index < orig.length) {
	    if (orig[index] == c) {
		index++;
	    } else {
		throw new ParseException("Wrong char", index);
	    }
	} else {
	    throw new ParseException("No more characters", index);
	}
    }

    /**
     * will only skip the current char if it matches the given
     * char
     */
    public boolean skipIfChar(char c) throws ParseException {
	if (index < orig.length) {
	    if (orig[index] == c) {
		index++;
		return true;
	    } else {
		return false;
	    }
	} else {
	    throw new ParseException("No more characters", index);
	}
    }
   	

    /**
     * current char must point to a number.  the number will be
     * parsed and the resulting number will be returned.  if a
     * number is not found, a ParseException will be thrown
     */
    public int parseNumber() throws ParseException {
	int length = orig.length;
	boolean gotNum = false;
	int result = 0;
				
	while (index < length) {
	    switch( orig[index] ) {
		case '0':
		    result *= 10;
		    gotNum = true;
		    break;
		    
		case '1':
		    result = result * 10 + 1;
		    gotNum = true;
		    break;
		    
		case '2':
		    result = result * 10 + 2;
		    gotNum = true;
		    break;
		    
		case '3':
		    result = result * 10 + 3;
		    gotNum = true;
		    break;
		    
		case '4':
		    result = result * 10 + 4;
		    gotNum = true;
		    break;
		    
		case '5':
		    result = result * 10 + 5;
		    gotNum = true;
		    break;
		    
		case '6':
		    result = result * 10 + 6;
		    gotNum = true;
		    break;
		    
		case '7':
		    result = result * 10 + 7;
		    gotNum = true;
		    break;
		    
		case '8':
		    result = result * 10 + 8;
		    gotNum = true;
		    break;
		    
		case '9':
		    result = result * 10 + 9;
		    gotNum = true;
		    break;
	
		default:
		    if (gotNum)
			return result;
		    else
			throw new ParseException("No Number found", index);
	    }
	    
	    index++;
	}
		
	// check the result
	if (gotNum)
	    return result;
			
	// else, throw a parse error
	throw new ParseException("No Number found", index);
    }
	

    /**
     * will look for one of "Jan/Feb/Mar/Apr/May/Jun/Jul/Aug/Sep/Oct/Nov/Dev"
     * and return the numerical version of the month. (0-11).  a ParseException
     * error is thrown if a month cannot be found.
     */
    public int parseMonth() throws ParseException {
	char curr;
		
	try {
	    switch(orig[index++]) {
	    case 'J':
	    case 'j': // "Jan" (0) /  "Jun" (5) /  "Jul" (6)
		// check next char
		switch(orig[index++]) {
		case 'A':
		case 'a':
		    curr = orig[index++];
		    if (curr == 'N' || curr == 'n') {
			return 0;
		    }
		    break;
							
		case 'U':
		case 'u':
		    curr = orig[index++];
		    if (curr == 'N' || curr == 'n') {
			return 5;
		    } else if (curr == 'L' || curr == 'l') {
			return 6;
		    }
		    break;
		}
		break;
				
	    case 'F':
	    case 'f': // "Feb"
		curr = orig[index++];
		if (curr == 'E' || curr == 'e') {
		    curr = orig[index++];
		    if (curr == 'B' || curr == 'b') {
			return 1;
		    }
		}
		break;
				
	    case 'M':
	    case 'm': // "Mar" (2) /  "May" (4)
		curr = orig[index++];
		if (curr == 'A' || curr == 'a') {
		    curr = orig[index++];
		    if (curr == 'R' || curr == 'r') {
			return 2;
		    } else if (curr == 'Y' || curr == 'y') {
			return 4;
		    }
		}
		break;			
				
	    case 'A':
	    case 'a': // "Apr" (3) /  "Aug" (7)
		curr = orig[index++];
		if (curr == 'P' || curr == 'p') {
		    curr = orig[index++];
		    if (curr == 'R' || curr == 'r') {
			return 3;
		    }
		} else if (curr == 'U' || curr == 'u') {
		    curr = orig[index++];
		    if (curr == 'G' || curr == 'g') {
			return 7;
		    }
		}
		break;			
				
	    case 'S':
	    case 's': // "Sep" (8)
		curr = orig[index++];
		if (curr == 'E' || curr == 'e') {
		    curr = orig[index++];
		    if (curr == 'P' || curr == 'p') {
			return 8;
		    }
		}
		break;			
				
	    case 'O':
	    case 'o': // "Oct"
		curr = orig[index++];
		if (curr == 'C' || curr == 'c') {
		    curr = orig[index++];
		    if (curr == 'T' || curr == 't') {
			return 9;
		    }
		}
		break;			
				
	    case 'N':
	    case 'n': // "Nov"
		curr = orig[index++];
		if (curr == 'O' || curr == 'o') {
		    curr = orig[index++];
		    if (curr == 'V' || curr == 'v') {
			return 10;
		    }
		}
		break;			

	    case 'D':
	    case 'd': // "Dec"
		curr = orig[index++];
		if (curr == 'E' || curr == 'e') {
		    curr = orig[index++];
		    if (curr == 'C' || curr == 'c') {
			return 11;
		    }
		}
		break;	
	    }
	} catch (ArrayIndexOutOfBoundsException e) {
	}
		
	throw new ParseException("Bad Month", index);
    }
	
    /**
     * will parse the timezone - either Numerical version (e.g. +0800, -0500)
     * or the alpha version (e.g. PDT, PST).  the result will be returned in
     * minutes needed to be added to the date to bring it to GMT.
     */
    public int parseTimeZone() throws ParseException {
	if (index >= orig.length) 
	    throw new ParseException("No more characters", index);
		
	char test = orig[index];
	if ( test == '+' || test == '-' ) {
	    return parseNumericTimeZone();
	} else {
	    return parseAlphaTimeZone();
	}
    }

    
    /**
     * will parse the Numerical time zone version (e.g. +0800, -0500)
     * the result will be returned in minutes needed to be added
     * to the date to bring it to GMT.
     */
    public int parseNumericTimeZone() throws ParseException {
	// we switch the sign if it is a '+'
	// since the time in the string we are
	// parsing is off from GMT by that amount.
	// and we want to get the time back into
	// GMT, so we substract it.
	boolean switchSign = false;
	char first = orig[index++];
	if (first == '+') {
	    switchSign = true;
	} else if (first != '-') {
	    throw new ParseException("Bad Numeric TimeZone", index);	
	}
		
	int tz = parseNumber();
	int offset = (tz / 100) * 60  + (tz % 100);
	if (switchSign) {
	    return -offset;
	} else {
	    return offset;
	}
    }

    /**
     * will parse the alpha time zone version (e.g. PDT, PST).
     * the result will be returned in minutes needed to be added
     * to the date to bring it to GMT.
     */
    public int parseAlphaTimeZone() throws ParseException {
	int result = 0;
	boolean foundCommon = false;
	char curr;
		
	try {
	    switch(orig[index++]) {
	    case 'U':
	    case 'u': // "UT"	/	Universal Time
		curr = orig[index++];
		if (curr == 'T' || curr == 't') {
		    result = 0;
		    break;
		}
		throw new ParseException("Bad Alpha TimeZone", index);
					
	    case 'G':
	    case 'g': // "GMT" ; Universal Time
		curr = orig[index++];
		if (curr == 'M' || curr == 'm') {
		    curr = orig[index++];
		    if (curr == 'T' || curr == 't') {
			result = 0;
			break;
		    }		
		}
		throw new ParseException("Bad Alpha TimeZone", index);	
					
	    case 'E':
	    case 'e': // "EST" / "EDT" ;  Eastern:  - 5/ - 4
		result = 300;
		foundCommon = true;
		break;
					
	    case 'C':
	    case 'c': // "CST" / "CDT" ;  Central:  - 6/ - 5
		result = 360;
		foundCommon = true;
		break;
					
	    case 'M':
	    case 'm': // "MST" / "MDT" ;  Mountain: - 7/ - 6
		result = 420;
		foundCommon = true;
		break;
					
	    case 'P':
	    case 'p': // "PST" / "PDT" ;  Pacific:  - 8/ - 7 
		result = 480;
		foundCommon = true;
		break;

	    default:
		throw new ParseException("Bad Alpha TimeZone", index);
	    }
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new ParseException("Bad Alpha TimeZone", index);
	}
		
	if (foundCommon) {
	    curr = orig[index++];
	    if (curr == 'S' || curr == 's') {
		curr = orig[index++];
		if (curr != 'T' && curr != 't') {
		    throw new ParseException("Bad Alpha TimeZone", index);
		}
	    } else if (curr == 'D' || curr == 'd') {
		curr = orig[index++];
		if (curr == 'T' || curr != 't') {
		    // for daylight time
		    result -= 60;
		} else {
		    throw new ParseException("Bad Alpha TimeZone", index);
		}
	    }
	}

	return result;
    }

    int getIndex() {
	return index;
    }
}
