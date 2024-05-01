package tn.dymes.store.entites;


import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.dymes.store.enums.Gender;

import javax.persistence.*;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable{

	@Id
	String id;
	@Enumerated(EnumType.STRING)
	Gender gender;
	String firstName;
	String lastName;
	String bio;
	String dob;
	@Column(unique=true)
	String email;
	boolean emailVerified;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	String password;
	String country;
	String address;
	String state;
	String city;
	String zipCode;
	String phone;
	String phone2;
	String profilePhoto;
	boolean suspension;
	Date create_account_date;
	@JsonIgnore
	private String temporaryActivationCode;
	@JsonIgnore
	private Instant temporaryActivationCodeTimeStamp;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_name")
	)
	List<Role> roles;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_authorization",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "authorization_id")
	)
	List<Authorization> authorizations;



}
