package com.app.dashchat.pojo;
// Generated Sep 25, 2020 2:22:03 PM by Hibernate Tools 4.3.1

import java.util.Map;

import javax.persistence.Entity;

import com.app.dashchat.util.AppParams;
import com.app.dashchat.util.ParamUtil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Users implements java.io.Serializable {

	private static final long serialVersionUID = -2983600627331475796L;
	private String id;
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private String state;
	private String address;
	private String phone;
	private String createdAt;
	private String updatedAt;
	private String lastLoggedIn;
	private String contact;

	public static Users fromMap(Map<String, Object> input) {
		Users obj = new Users();
		obj.setId(ParamUtil.getString(input, AppParams.S_ID));
		obj.setUsername(ParamUtil.getString(input, AppParams.S_USER_NAME));
		obj.setPassword(ParamUtil.getString(input, AppParams.S_PASSWORD));
		obj.setEmail(ParamUtil.getString(input, AppParams.S_EMAIL));
		obj.setFirstName(ParamUtil.getString(input, AppParams.S_FIRST_NAME));
		obj.setLastName(ParamUtil.getString(input, AppParams.S_LAST_NAME));
		obj.setState(ParamUtil.getString(input, AppParams.S_STATE));
		obj.setAddress(ParamUtil.getString(input, AppParams.S_ADDRESS));
		obj.setPhone(ParamUtil.getString(input, AppParams.S_PHONE));
		obj.setCreatedAt(ParamUtil.getString(input, AppParams.D_CREATED_AT));
		obj.setUpdatedAt(ParamUtil.getString(input, AppParams.D_UPDATED_AT));
		obj.setLastLoggedIn(ParamUtil.getString(input, AppParams.D_LAST_LOGGED_IN));
		obj.setContact(ParamUtil.getString(input, AppParams.S_CONTACT));
		return obj;
	}

	@Override
	public String toString() {
		return "UserObj [id = " + id + ",username = " + username + ",password = " + password + ",email = " + email
				+ ",firstName = " + firstName + ",lastName = " + lastName + ",state = " + state + ",address = "
				+ address + ",phone = " + phone + ",createdAt = " + createdAt + ",updatedAt = " + updatedAt
				+ ",lastLoggedIn = " + lastLoggedIn + ",contact = " + contact + "]";
	}

}
