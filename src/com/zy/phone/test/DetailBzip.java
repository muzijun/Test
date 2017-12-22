package com.zy.phone.test;

import java.util.List;
import java.util.Map;

public class DetailBzip {

	public String AdsId;
	public String Title;
	public String Logo;
	public String Size;
	public String Intro;
	public String PackName;
	public int Price;
	public String is_register;
	public String Day;
	public List<String> IntroImg;
	public Map<String, DetailBzip.Task> Task;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AdsId: " + AdsId + ",Title:" + Title + ",Logo:"+Logo+",Size:" +Size+",Intro:"+Intro+",PackName:"+PackName+",Price:"+Price+",is_register:"
				+is_register + ", IntroImg:" + IntroImg;
	}

	public String getDay() {
		return Day;
	}



	public void setDay(String day) {
		Day = day;
	}



	public List<String> getIntroImg() {
		return IntroImg;
	}



	public void setIntroImg(List<String> introImg) {
		IntroImg = introImg;
	}



	public String getAdsId() {
		return AdsId;
	}

	public void setAdsId(String adsId) {
		AdsId = adsId;
	}



	public String getTitle() {
		return Title;
	}



	public void setTitle(String title) {
		Title = title;
	}



	public String getLogo() {
		return Logo;
	}



	public void setLogo(String logo) {
		Logo = logo;
	}



	public String getSize() {
		return Size;
	}



	public void setSize(String size) {
		Size = size;
	}



	public String getIntro() {
		return Intro;
	}



	public void setIntro(String intro) {
		Intro = intro;
	}



	public String getPackName() {
		return PackName;
	}



	public void setPackName(String packName) {
		PackName = packName;
	}



	public int getPrice() {
		return Price;
	}



	public void setPrice(int price) {
		Price = price;
	}



	public String getIs_register() {
		return is_register;
	}



	public void setIs_register(String is_register) {
		this.is_register = is_register;
	}



	public Map<String, DetailBzip.Task> getTask() {
		return Task;
	}



	public void setTask(Map<String, DetailBzip.Task> task) {
		Task = task;
	}

	class Task{
		public String Day;
		public int TaskMoney;
		public String TaskIntro;
		public String getDay() {
			return Day;
		}
		public void setDay(String day) {
			Day = day;
		}
		public int getTaskMoney() {
			return TaskMoney;
		}
		public void setTaskMoney(int taskMoney) {
			TaskMoney = taskMoney;
		}
		public String getTaskIntro() {
			return TaskIntro;
		}
		public void setTaskIntro(String taskIntro) {
			TaskIntro = taskIntro;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "Day:" + Day + ",TaskMoney:" + TaskMoney + ",TaskIntro" + TaskIntro;
		}
	}
}
