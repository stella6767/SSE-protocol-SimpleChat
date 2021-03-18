package com.example.reactorex02;


public class TestSplit {
	public static void main(String[] args) {
		String response = "username=ssar&password=1234";		
		System.out.println(response);
		
		String[] respArray = response.split("&");
		
		String username = (respArray[0].split("="))[1];
		String password = (respArray[1].split("="))[1];
		System.out.println(username+"   "+password);
		
		User user = new User(username, password);
		System.out.println(user);
		
		
	}

}
