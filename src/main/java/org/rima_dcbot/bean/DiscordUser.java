package org.rima_dcbot.bean;

import java.util.Objects;

import net.dv8tion.jda.api.entities.User;

public class DiscordUser {

	private String username;
	private String discriminator;

	public DiscordUser() {
	}
	
	public DiscordUser(User user) {
		this.username = user.getName();
		this.discriminator = user.getDiscriminator();
	}
	
	public DiscordUser(String username, String discriminator) {
		this.username = username;
		this.discriminator = discriminator;
	}

	public String getUsername() {
		return this.username;
	}

	public String getDiscriminator() {
		return this.discriminator;
	}
	
	public DiscordUser copy() {
		return new DiscordUser(this.username, this.discriminator);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DiscordUser) {
			return equals((DiscordUser) obj);
		}
		return false;
	}
	
	private boolean equals(DiscordUser u) {
		
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
