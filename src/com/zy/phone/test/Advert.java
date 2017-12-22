package com.zy.phone.test;

public class Advert {

	public int AdsId;
	public String Title;
	public String Logo;
	public String Detail;
	public String PackName;
	public String Size;
	public int Price;
	public String is_register;
	public int TaskSign;
	public String DetailUrl;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AdsId:" + AdsId + ",Title:" + Title + ",Logo:"+Logo+",Detail"+Detail+",PackName:"+PackName+",Size:"+Size+",Price:"+Price+",is_register:"+is_register+"is_DetailUrl"+DetailUrl;
	}

	public int getAdsId() {
		return AdsId;
	}

	public void setAdsId(int adsId) {
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

	public String getDetail() {
		return Detail;
	}

	public void setDetail(String detail) {
		Detail = detail;
	}

	public String getPackName() {
		return PackName;
	}

	public void setPackName(String packName) {
		PackName = packName;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
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

	public int getTaskSign() {
		return TaskSign;
	}

	public void setTaskSign(int taskSign) {
		TaskSign = taskSign;
	}

	public String getDetailUrl() {
		return DetailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		DetailUrl = detailUrl;
	}
	
	
	
}
