package com.github.harshith;

public class StringInputStream {
	public int count = 0;
	public char[] data = new char[1024];
	
	public StringInputStream(String str){
		process(str);
	}
	
	private void process(String str){
		data = new char[str.length()];
		if(str.length()==0) return;
		for(int i = 0; i < data.length; i++){
			data[i] = str.charAt(i);
		}
	}
	
	public void clear(){
		process("");
	}
	
	public void setData(String str){
		process(str);
	}
	
	public int read(){
		count++;
		if(count > data.length){
			count = data.length;
		}
		return (int) data[count-1];
	}
}