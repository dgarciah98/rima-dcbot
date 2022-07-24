package org.rima_dcbot.bean;

import java.util.Objects;

import net.dv8tion.jda.api.entities.User;

public class BlacklistedUser {

	private String username;
	private String discriminator;

	public BlacklistedUser() {
	}
	
	public BlacklistedUser(User user) {
		this.username = user.getName();
		this.discriminator = user.getDiscriminator();
	}
	
	public BlacklistedUser(String username, String discriminator) {
		this.username = username;
		this.discriminator = discriminator;
	}

	public String getUsername() {
		return this.username;
	}

	public String getDiscriminator() {
		return this.discriminator;
	}
	
	public BlacklistedUser copy() {
		return new BlacklistedUser(this.username, this.discriminator);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlacklistedUser) {
			return equals((BlacklistedUser) obj);
		}
		return false;
	}
	
	private boolean equals(BlacklistedUser u) {
		
		boolean equalUsername = false;
		if (u.getUsername() != null) {
			equalUsername = u.getUsername().equals(this.username);
		}
		
		boolean equalDiscriminator = false;
		if (u.getDiscriminator() != null) {
			equalDiscriminator = u.getDiscriminator().equals(this.discriminator);
		}
		
		return equalUsername && equalDiscriminator;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.username, this.discriminator);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.username);
		sb.append("#");
		sb.append(this.discriminator);
		
		return sb.toString();
	}
}
