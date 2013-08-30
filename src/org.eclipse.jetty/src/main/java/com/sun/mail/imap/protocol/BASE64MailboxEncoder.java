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
 * @(#)BASE64MailboxEncoder.java	1.8 07/05/04
 */

package com.sun.mail.imap.protocol;

import java.io.*;


/**
 *
 *
  from RFC2060

5.1.3.  Mailbox International Naming Convention

   By convention, international mailbox names are specified using a
   modified version of the UTF-7 encoding described in [UTF-7].  The
   purpose of these modifications is to correct the following problems
   with UTF-7:

      1) UTF-7 uses the "+" character for shifting; this conflicts with
         the common use of "+" in mailbox names, in particular USENET
         newsgroup names.

      2) UTF-7's encoding is BASE64 which uses the "/" character; this
         conflicts with the use of "/" as a popular hierarchy delimiter.

      3) UTF-7 prohibits the unencoded usage of "\"; this conflicts with
         the use of "\" as a popular hierarchy delimiter.

      4) UTF-7 prohibits the unencoded usage of "~"; this conflicts with
         the use of "~" in some servers as a home directory indicator.

      5) UTF-7 permits multiple alternate forms to represent the same
         string; in particular, printable US-ASCII chararacters can be
         represented in encoded form.

   In modified UTF-7, printable US-ASCII characters except for "&"
   represent themselves; that is, characters with octet values 0x20-0x25
   and 0x27-0x7e.  The character "&" (0x26) is represented by the two-
   octet sequence "&-".

   All other characters (octet values 0x00-0x1f, 0x7f-0xff, and all
   Unicode 16-bit octets) are represented in modified BASE64, with a
   further modification from [UTF-7] that "," is used instead of "/".
   Modified BASE64 MUST NOT be used to represent any printing US-ASCII
   character which can represent itself.

   "&" is used to shift to modified BASE64 and "-" to shift back to US-
   ASCII.  All names start in US-ASCII, and MUST end in US-ASCII (that
   is, a name that ends with a Unicode 16-bit octet MUST end with a "-
   ").





Crispin                     Standards Track                    [Page 15]

RFC 2060                       IMAP4rev1                   December 1996


      For example, here is a mailbox name which mixes English, Japanese,
      and Chinese text: ~peter/mail/&ZeVnLIqe-/&U,BTFw-


 * This class will do the correct Encoding for the IMAP mailboxes
 *
 * @version	1.8, 07/05/04
 * @author	Christopher Cotton
 */

public class BASE64MailboxEncoder {
    protected byte[] buffer = new byte[4];
    protected int bufsize = 0;
    protected boolean started = false;
    protected Writer out = null;
    

    public static String encode(String original) {
	BASE64MailboxEncoder base64stream = null;
	char origchars[] = original.toCharArray();
	int length = origchars.length;
	boolean changedString = false;
	CharArrayWriter writer = new CharArrayWriter(length);
	
	// loop over all the chars
	for(int index = 0; index < length; index++) {
	    char current = origchars[index];

	    // octets in the range 0x20-0x25,0x27-0x7e are themselves
	    // 0x26 "&" is represented as "&-"
	    if (current >= 0x20 && current <= 0x7e) {
		if (base64stream != null) {
		    base64stream.flush();
		}
		
		if (current == '&') {
		    changedString = true;
		    writer.write('&');
		    writer.write('-');
		} else {
		    writer.write(current);
		}
	    } else {

		// use a B64MailboxEncoder to write out the other bytes
		// as a modified BASE64.  The stream will write out
		// the beginning '&' and the ending '-' which is part
		// of every encoding.

		if (base64stream == null) {
		    base64stream = new BASE64MailboxEncoder(writer);
		    changedString = true;
		}
		
		base64stream.write(current);
	    }
	}


	if (base64stream != null) {
	    base64stream.flush();
	}

	if (changedString) {
	    return writer.toString();
	} else {
	    return original;
	}
    }


    /**
     * Create a BASE64 encoder
     */
    public BASE64MailboxEncoder(Writer what) {
	out = what;
    }

    public void write(int c) {
	try {
	    // write out the initial character if this is the first time
	    if (!started) {
		started = true;
		out.write('&');
	    }
	
	    // we write each character as a 2 byte unicode character
	    buffer[bufsize++] = (byte) (c >> 8);
	    buffer[bufsize++] = (byte) (c & 0xff);

	    if (bufsize >= 3) {
		encode();
		bufsize -= 3;
	    }
	} catch (IOException e) {
	    //e.printStackTrace();
	}
    }
    

    public void flush() {
	try {
	    // flush any bytes we have
	    if (bufsize > 0) {
		encode();
		bufsize = 0;
	    }

	    // write the terminating character of the encoding
	    if (started) {
		out.write('-');
		started = false;
	    }
	} catch (IOException e) {
	    //e.printStackTrace();
	}
    }


    protected void encode() throws IOException {
	byte a, b, c;
	if (bufsize == 1) {
	    a = buffer[0];
	    b = 0;
	    c = 0;
	    out.write(pem_array[(a >>> 2) & 0x3F]);
	    out.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
		// no padding characters are written
	} else if (bufsize == 2) {
	    a = buffer[0];
	    b = buffer[1];
	    c = 0;
	    out.write(pem_array[(a >>> 2) & 0x3F]);
	    out.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
	    out.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
		// no padding characters are written
	} else {
	    a = buffer[0];
	    b = buffer[1];
	    c = buffer[2];
	    out.write(pem_array[(a >>> 2) & 0x3F]);
	    out.write(pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)]);
	    out.write(pem_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)]);
	    out.write(pem_array[c & 0x3F]);

	    // copy back the extra byte
	    if (bufsize == 4)
		buffer[0] = buffer[3];
        }
    }

    private final static char pem_array[] = {
	'A','B','C','D','E','F','G','H', // 0
	'I','J','K','L','M','N','O','P', // 1
	'Q','R','S','T','U','V','W','X', // 2
	'Y','Z','a','b','c','d','e','f', // 3
	'g','h','i','j','k','l','m','n', // 4
	'o','p','q','r','s','t','u','v', // 5
	'w','x','y','z','0','1','2','3', // 6
	'4','5','6','7','8','9','+',','  // 7
    };
}
