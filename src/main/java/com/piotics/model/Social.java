package com.piotics.model;

//import javax.persistence.MappedSuperclass;

//@MappedSuperclass
public class Social {

	private int likes;
	private int views;
	private int totalShares;

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getTotalShares() {
		return totalShares;
	}

	public void setTotalShares(int totalShares) {
		this.totalShares = totalShares;
	}

}
