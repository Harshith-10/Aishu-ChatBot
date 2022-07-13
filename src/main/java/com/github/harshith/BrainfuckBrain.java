package com.github.harshith;

import java.io.OutputStream;

public class BrainfuckBrain {
	private BrainfuckEngine engine;
	private String outputData = "";
	private OutputStream out = new OutputStream(){
		@Override
		public void write(int b){
			outputData = outputData.concat(Character.toString((char) b));
		}
	};
	private StringInputStream in = new StringInputStream("");
	
	public BrainfuckBrain(){
		engine = new BrainfuckEngine(30000, out, in);
	}
	
	public String interpret(String code, String input){
		outputData = "";
		engine.interpret(code, input);
		return outputData;
	}
}