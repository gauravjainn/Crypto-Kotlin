package com.siltech.cryptochat.news;

import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("code")
	private String code;

	@SerializedName("hint")
	private Object hint;

	@SerializedName("details")
	private String details;

	@SerializedName("message")
	private String message;

	public String getCode(){
		return code;
	}

	public Object getHint(){
		return hint;
	}

	public String getDetails(){
		return details;
	}

	public String getMessage(){
		return message;
	}
}