package com.project.myapp;

public class SpinnerModel {
	private String Nationality = "";
	private int flag = 0;

	/*********** Set Methods ******************/
	public void setNationality(String str) {
		Nationality = str;
	}

	public void setFlag(int image) {
		flag = image;
	}

	/*********** Get Methods ****************/
	public String getNationality() {
		return Nationality;
	}

	public int getFlag() {
		return flag;
	}

}
