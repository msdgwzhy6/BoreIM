package com.boredream.im.entity;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * ����BmobChatUser����������������Ҫ���ӵ����Կ��ڴ�����
 */
public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ��ʾ����ƴ��������ĸ
	 */
	private String sortLetters;

	/**
	 * �Ա�-true-��
	 */
	private boolean sex;

	private BmobRelation blogs;

	/**
	 * ��������
	 */
	private BmobGeoPoint location;

	public BmobRelation getBlogs() {
		return blogs;
	}

	public void setBlogs(BmobRelation blogs) {
		this.blogs = blogs;
	}

	public BmobGeoPoint getLocation() {
		return location;
	}

	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}