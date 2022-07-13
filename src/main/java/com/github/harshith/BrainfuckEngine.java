package com.github.harshith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class BrainfuckEngine {
	protected byte[] data;
	protected int dataPointer = 0;
	protected int charPointer = 0;
	protected BufferedReader fileReader;
	protected StringInputStream consoleReader;
	protected OutputStream outWriter;
	protected int lineCount = 0;
	protected int columnCount = 0;
	
	protected static class Token {
		public final static char NEXT = '>';
		public final static char PREVIOUS = '<';
		public final static char PLUS = '+';
		public final static char MINUS = '-';
		public final static char OUTPUT = '.';
		public final static char INPUT = ',';
		public final static char BRACKET_LEFT = '[';
		public final static char BRACKET_RIGHT = ']';
	}
	
	public BrainfuckEngine(int cells, OutputStream out, StringInputStream in) {
		initate(cells);
		outWriter = out;
		consoleReader = in;
	}
	
	protected void initate(int cells) {
		data = new byte[cells];
		dataPointer = 0;
		charPointer = 0;
	}
	
	public void interpret(File file) throws Exception {
		fileReader = new BufferedReader(new FileReader(file));
		String content = "";
		String line = "";
		while((line = fileReader.readLine()) != null) {
			content += line;
			lineCount++;
		}
		interpret(content, "");
	}
	
	public void interpret(String str, String input) {
		initate(data.length);
		consoleReader.setData(input);
		for (; charPointer < str.length(); charPointer++) 
			interpret(str.charAt(charPointer), str.toCharArray());
	}
	
	protected void interpret(char c, char[] chars) {
		switch (c) {
		case Token.NEXT:
			if ((dataPointer + 1) > data.length) {
				try {
					outWriter.write(("Error on line " + lineCount + ", column " + columnCount + ":"
							+ "data pointer (" + dataPointer
							+ ") on postion " + charPointer + "" + " out of range.").getBytes());
				}catch(Exception e){

				}
			}
			dataPointer++;
			break;
			
		case Token.PREVIOUS:
			if ((dataPointer - 1) < 0) {
				try {
					outWriter.write(("Error on line " + lineCount + ", column " + columnCount + ":"
							+ "data pointer (" + dataPointer
							+ ") on postion " + charPointer + "" + " out of range.").getBytes());
				}catch(Exception e){

				}
			}
			dataPointer--;
			break;
			
		case Token.PLUS:
			data[dataPointer]++;
			break;
			
		case Token.MINUS:
			if (data[dataPointer] > 0) {
				data[dataPointer]--;
			}
			break;
			
		case Token.OUTPUT:
			try {
				outWriter.write((char) data[dataPointer]);
			}catch(Exception e){}
			break;
			
		case Token.INPUT:
			data[dataPointer] = (byte) consoleReader.read();
			break;
			
		case Token.BRACKET_LEFT:
			if (data[dataPointer] == 0) {
				int i = 1;
				while (i > 0) {
					char c2 = chars[++charPointer];
					if (c2 == Token.BRACKET_LEFT)
						i++;
					else if (c2 == Token.BRACKET_RIGHT)
						i--;
				}
			}
			break;
			
		case Token.BRACKET_RIGHT:
			int i = 1;
			while (i > 0) {
				char c2 = chars[--charPointer];
				if (c2 == Token.BRACKET_LEFT)
					i--;
				else if (c2 == Token.BRACKET_RIGHT)
					i++;
			}
			charPointer--;
			break;
		}
		columnCount++;
	}
}