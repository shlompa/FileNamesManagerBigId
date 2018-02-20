/**
 * 
 */
package com.filenames.manager;

public class TextLocation {
	
	private long lineOffset;
	private long charOffset;
	
	
	public TextLocation(long lineOffset, long charOffset) {
		this.lineOffset = lineOffset;
		this.charOffset = charOffset;
	}
	public long getLineOffset() {
		return lineOffset;
	}
	public void setLineOffset(long lineOffset) {
		this.lineOffset = lineOffset;
	}
	public long getCharOffset() {
		return charOffset;
	}
	public void setCharOffset(long charOffset) {
		this.charOffset = charOffset;
	}
	@Override
	public String toString() {
		return "TextLocation [lineOffset=" + lineOffset + ", charOffset=" + charOffset + "]";
	}

}
