/*
 * Copyright (c) 2010-2012 Nathan Rajlich
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.java_websocket.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;

public class Charsetfunctions {

	public static CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;

	/*
	* @return UTF-8 encoding in bytes
	*/
	public static byte[] utf8Bytes( String s ) {
		try {
			return s.getBytes( "UTF8" );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	/*
	* @return ASCII encoding in bytes
	*/
	public static byte[] asciiBytes( String s ) {
		try {
			return s.getBytes( "ASCII" );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public static String stringAscii( byte[] bytes ) {
		return stringAscii( bytes, 0, bytes.length );
	}
	
	public static String stringAscii( byte[] bytes, int offset, int length ){
		try {
			return new String( bytes, offset, length, "ASCII" );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public static String stringUtf8( byte[] bytes ) throws InvalidDataException {
		return stringUtf8( ByteBuffer.wrap( bytes ) );
	}

	/*public static String stringUtf8( byte[] bytes, int off, int length ) throws InvalidDataException {
		CharsetDecoder decode = Charset.forName( "UTF8" ).newDecoder();
		decode.onMalformedInput( codingErrorAction );
		decode.onUnmappableCharacter( codingErrorAction );
		//decode.replaceWith( "X" );
		String s;
		try {
			s = decode.decode( ByteBuffer.wrap( bytes, off, length ) ).toString();
		} catch ( CharacterCodingException e ) {
			throw new InvalidDataException( CloseFrame.NO_UTF8, e );
		}
		return s;
	}*/

	public static String stringUtf8( ByteBuffer bytes ) throws InvalidDataException {
		CharsetDecoder decode = Charset.forName( "UTF8" ).newDecoder();
		decode.onMalformedInput( codingErrorAction );
		decode.onUnmappableCharacter( codingErrorAction );
		// decode.replaceWith( "X" );
		String s;
		try {
			bytes.mark();
			s = decode.decode( bytes ).toString();
			bytes.reset();
		} catch ( CharacterCodingException e ) {
			throw new InvalidDataException( CloseFrame.NO_UTF8, e );
		}
		return s;
	}

	public static void main( String[] args ) throws InvalidDataException {
		stringUtf8( utf8Bytes( "\0" ) );
		stringAscii( asciiBytes( "\0" ) );
	}

}
