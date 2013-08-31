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
 * @(#)IMAPSaslAuthenticator.java	1.10 07/05/04
 */

package com.sun.mail.imap.protocol;

import java.io.*;
import java.util.*;
import javax.security.sasl.*;
import javax.security.auth.callback.*;

import com.sun.mail.iap.*;
import com.sun.mail.imap.*;
import com.sun.mail.util.*;

/**
 * This class contains a single method that does authentication using
 * SASL.  This is in a separate class so that it can be compiled with
 * J2SE 1.5.  Eventually it should be merged into IMAPProtocol.java.
 */

public class IMAPSaslAuthenticator implements SaslAuthenticator {

    private IMAPProtocol pr;
    private String name;
    private Properties props;
    private boolean debug;
    private PrintStream out;
    private String host;

    public IMAPSaslAuthenticator(IMAPProtocol pr, String name, Properties props,
				boolean debug, PrintStream out, String host) {
	this.pr = pr;
	this.name = name;
	this.props = props;
	this.debug = debug;
	this.out = out;
	this.host = host;
    }

    public boolean authenticate(String[] mechs, String realm, String authzid,
				String u, String p) throws ProtocolException {

	synchronized (pr) {	// authenticate method should be synchronized
	Vector v = new Vector();
	String tag = null;
	Response r = null;
	boolean done = false;
	if (debug) {
	    out.print("IMAP SASL DEBUG: Mechanisms:");
	    for (int i = 0; i < mechs.length; i++)
		out.print(" " + mechs[i]);
	    out.println();
	}

	SaslClient sc;
	final String r0 = realm;
	final String u0 = u;
	final String p0 = p;
	CallbackHandler cbh = new CallbackHandler() {
	    public void handle(Callback[] callbacks) {
		if (debug)
		    out.println("IMAP SASL DEBUG: callback length: " +
							callbacks.length);
		for (int i = 0; i < callbacks.length; i++) {
		    if (debug)
			out.println("IMAP SASL DEBUG: callback " + i + ": " +
							callbacks[i]);
		    if (callbacks[i] instanceof NameCallback) {
			NameCallback ncb = (NameCallback)callbacks[i];
			ncb.setName(u0);
		    } else if (callbacks[i] instanceof PasswordCallback) {
			PasswordCallback pcb = (PasswordCallback)callbacks[i];
			pcb.setPassword(p0.toCharArray());
		    } else if (callbacks[i] instanceof RealmCallback) {
			RealmCallback rcb = (RealmCallback)callbacks[i];
			rcb.setText(r0 != null ?
			    r0 : rcb.getDefaultText());
		    } else if (callbacks[i] instanceof RealmChoiceCallback) {
			RealmChoiceCallback rcb =
			    (RealmChoiceCallback)callbacks[i];
			if (r0 == null)
			    rcb.setSelectedIndex(rcb.getDefaultChoice());
			else {
			    // need to find specified realm in list
			    String[] choices = rcb.getChoices();
			    for (int k = 0; k < choices.length; k++) {
				if (choices[k].equals(r0)) {
				    rcb.setSelectedIndex(k);
				    break;
				}
			    }
			}
		    }
		}
	    }
	};

	try {
	    sc = Sasl.createSaslClient(mechs, authzid, name, host,
					(Map)props, cbh);
	} catch (SaslException sex) {
	    if (debug)
		out.println("IMAP SASL DEBUG: Failed to create SASL client: " +
								sex);
	    return false;
	}
	if (sc == null) {
	    if (debug)
		out.println("IMAP SASL DEBUG: No SASL support");
	    return false;
	}
	if (debug)
	    out.println("IMAP SASL DEBUG: SASL client " +
						sc.getMechanismName());

	try {
	    tag = pr.writeCommand("AUTHENTICATE " + sc.getMechanismName(),
						null);
	} catch (Exception ex) {
	    if (debug)
		out.println("IMAP SASL DEBUG: AUTHENTICATE Exception: " + ex);
	    return false;
	}

	OutputStream os = pr.getIMAPOutputStream(); // stream to IMAP server

	/*
	 * Wrap a BASE64Encoder around a ByteArrayOutputstream
	 * to craft b64 encoded username and password strings
	 *
	 * Note that the encoded bytes should be sent "as-is" to the
	 * server, *not* as literals or quoted-strings.
	 *
	 * Also note that unlike the B64 definition in MIME, CRLFs 
	 * should *not* be inserted during the encoding process. So, I
	 * use Integer.MAX_VALUE (0x7fffffff (> 1G)) as the bytesPerLine,
	 * which should be sufficiently large !
	 */

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte[] CRLF = { (byte)'\r', (byte)'\n'};

	// Hack for Novell GroupWise XGWTRUSTEDAPP authentication mechanism
	boolean isXGWTRUSTEDAPP = sc.getMechanismName().equals("XGWTRUSTEDAPP");
	while (!done) { // loop till we are done
	    try {
		r = pr.readResponse();
	    	if (r.isContinuation()) {
		    byte[] ba = null;
		    if (!sc.isComplete()) {
			ba = r.readByteArray().getNewBytes();
			if (ba.length > 0)
			    ba = BASE64DecoderStream.decode(ba);
			if (debug)
			    out.println("IMAP SASL DEBUG: challenge: " +
				ASCIIUtility.toString(ba, 0, ba.length) + " :");
			ba = sc.evaluateChallenge(ba);
		    }
		    if (ba == null) {
			if (debug)
			    out.println("IMAP SASL DEBUG: no response");
			os.write(CRLF); // write out empty line
			os.flush(); 	// flush the stream
			bos.reset(); 	// reset buffer
		    } else {
			if (debug)
			    out.println("IMAP SASL DEBUG: response: " +
				ASCIIUtility.toString(ba, 0, ba.length) + " :");
			ba = BASE64EncoderStream.encode(ba);
			if (isXGWTRUSTEDAPP)
			    bos.write("XGWTRUSTEDAPP ".getBytes());
			bos.write(ba);

			bos.write(CRLF); 	// CRLF termination
			os.write(bos.toByteArray()); // write out line
			os.flush(); 	// flush the stream
			bos.reset(); 	// reset buffer
		    }
		} else if (r.isTagged() && r.getTag().equals(tag))
		    // Ah, our tagged response
		    done = true;
		else if (r.isBYE()) // outta here
		    done = true;
		else // hmm .. unsolicited response here ?!
		    v.addElement(r);
	    } catch (Exception ioex) {
		if (debug)
		    ioex.printStackTrace();
		// convert this into a BYE response
		r = Response.byeResponse(ioex);
		done = true;
		// XXX - ultimately return true???
	    }
	}

	if (sc.isComplete() /*&& res.status == SUCCESS*/) {
	    String qop = (String)sc.getNegotiatedProperty(Sasl.QOP);
	    if (qop != null && (qop.equalsIgnoreCase("auth-int") ||
				qop.equalsIgnoreCase("auth-conf"))) {
		// XXX - NOT SUPPORTED!!!
		if (debug)
		    out.println("IMAP SASL DEBUG: " +
			"Mechanism requires integrity or confidentiality");
		return false;
	    }
	}

	/* Dispatch untagged responses.
	 * NOTE: in our current upper level IMAP classes, we add the
	 * responseHandler to the Protocol object only *after* the 
	 * connection has been authenticated. So, for now, the below
	 * code really ends up being just a no-op.
	 */
	Response[] responses = new Response[v.size()];
	v.copyInto(responses);
	pr.notifyResponseHandlers(responses);

	// Handle the final OK, NO, BAD or BYE response
	pr.handleResult(r);
	pr.setCapabilities(r);
	return true;
    }
    }
}
