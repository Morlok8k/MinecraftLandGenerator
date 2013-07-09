/*
This work (and included software, documentation such as READMEs, or other related items) is being provided by the copyright holders under the following license.
License
By obtaining, using and/or copying this work, you (the licensee) agree that you have read, understood, and will comply with the following terms and conditions.
Permission to copy, modify, and distribute this software and its documentation, with or without modification, for any purpose and without fee or royalty is hereby granted, provided that you include the following on ALL copies of the software and documentation or portions thereof, including modifications:
The full text of this NOTICE in a location viewable to users of the redistributed or derivative work.
Any pre-existing intellectual property disclaimers, notices, or terms and conditions. If none exist, the W3C Software Short Notice should be included (hypertext is preferred, text is permitted) within the body of any redistributed or derivative code.
Notice of any changes or modifications to the files, including the date changes were made. (We recommend you provide URIs to the location from which the code is derived.)
Disclaimers
THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
The name and trademarks of copyright holders may NOT be used in advertising or publicity pertaining to the software without specific, written prior permission. Title to copyright in this software and any associated documentation will at all times remain with copyright holders.
*/

package org.w3c.bert_bos.UTF8URL;

/**
 * 
 * @author morlok8k
 */
public class Unescape {

	/* Morlok8k:
	 * Just a note about this unescape method:
	 * I am quite amazed at this code.  It is vastly superior to my own coding ability.
	 * I had to look up many things in it to see how it works, and I still don't quite follow it.
	 * (I have never worked with raw bytes in Java... Never needed to.)
	 * But it works and it works very well.   I tip my hat at you Bert!
	 *
	 * I just needed to say this somewhere, even though very few people will ever read this.
	 */
	/**
	 * Created: 17 April 1997<br>
	 * Author: Bert Bos &lt;<a href="mailto:bert@w3.org">bert@w3.org</a>&gt;<br>
	 * <br>
	 * unescape: <a href="http://www.w3.org/International/unescape.java">http://www.w3.org/International/unescape.java</a><br>
	 * <br>
	 * Copyright &copy; 1997 World Wide Web Consortium, (Massachusetts Institute of Technology, European Research Consortium for Informatics and Mathematics, Keio University). All Rights Reserved.
	 * This work is distributed under the W3C&reg; Software License [1] in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE.<br>
	 * <br>
	 * [1] <a href="http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231">http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231</a>
	 * 
	 * @param s
	 *            string of URL
	 * @return decoded string of URL
	 * @author Bert Bos
	 */
	public static String unescape(final String s) {
		final StringBuffer sbuf = new StringBuffer();
		final int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
				case '%':
					ch = s.charAt(++i);
					final int hb =
							(Character.isDigit((char) ch) ? ch - '0' : (10 + Character
									.toLowerCase((char) ch)) - 'a') & 0xF;
					ch = s.charAt(++i);
					final int lb =
							(Character.isDigit((char) ch) ? ch - '0' : (10 + Character
									.toLowerCase((char) ch)) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				case '+':
					b = ' ';
					break;
				default:
					b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f);	// Add 6 bits to sumb
				if (--more == 0) {
					sbuf.append((char) sumb); // Add char to sbuf
				}
			} else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b);			// Store in sbuf
			} else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1;				// Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2;				// Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3;				// Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4;				// Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/{	// 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5;				// Expect 5 more bytes
			}
			/* We don't test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();
	}
}
