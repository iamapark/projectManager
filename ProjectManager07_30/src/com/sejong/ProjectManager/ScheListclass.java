package com.sejong.ProjectManager;

class ScheListClass{
	String ScheName = "";
	String ScheKey = "";
	int trashImage = R.id.bottomImage;

	public ScheListClass(String ScheName, String ScheKey){
		this.ScheName = ScheName;
		this.ScheKey = ScheKey;
	}

	public String getScheName(){
		return ScheName;
	}
	public String getScheKey(){
		return ScheKey;
	}
	public int getTrashImage(){
		return trashImage;
	}
}