package org.zbus.rpc.biz;

import java.util.List;


public class User{
	private String name;
	private String password;
	private int age;
	private String item; 
	private List<String> roles;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	} 
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", password=" + password + ", age=" + age
				+ ", item=" + item + ", roles=" + roles + "]";
	}
	
	
	
}