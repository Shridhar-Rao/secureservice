package com.shri.secureservice.entity;

import java.util.Set;


import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
		name ="users",
		uniqueConstraints = @UniqueConstraint(
				name= "unique_username",
				columnNames="username"
		)
	)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String username;
	
	private String password;//should be in encrypted form
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "users_roles", 
				joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
			)
	private Set<Role> roles;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "user")
	private Token token;

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
}
