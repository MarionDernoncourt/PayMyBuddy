package com.paymybuddy.backend.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "password", length = 255)
	private String password;
	
	@Column(name="account_balance", precision = 10, scale = 2)
	private BigDecimal accountBalance = BigDecimal.ZERO;


	@ManyToMany(
			fetch = FetchType.LAZY, 
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			})
			
	@JoinTable(
			name = "user_user", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "connected_user_id"))
	List<User> friends = new ArrayList<>();

	@ManyToMany(
			mappedBy = "friends")
	private List<User> friendsOf = new ArrayList<>();
	
	public void addFriend(User user) {
		friends.add(user);
	user.getFriends().add(this);
	}
	
	public void removeFriend(User user) {
		friends.remove(user);
		user.getFriends().remove(this);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<User> getFriends() {
		return friends;
	}

	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	public List<User> getFriendsOf() {
		return friendsOf;
	}

	public void setFriendsOf(List<User> friendsOf) {
		this.friendsOf = friendsOf;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

}
