package com.shri.secureservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
		name ="tokens"
	)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String authtoken;

	private LocalDateTime startdatetime;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	public Token(String authtoken, LocalDateTime startdatetime, User user) {
		super();
		this.authtoken = authtoken;
		this.startdatetime = startdatetime;
		this.user = user;
	}
}
