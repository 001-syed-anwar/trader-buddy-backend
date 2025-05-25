package com.traderbuddy.auth.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.traderbuddy.models.BaseAuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Data
@Entity
@Table(name = "users")
@SuperBuilder
public class User extends BaseAuditingEntity implements UserDetails {
	private static final int LAST_ACTIVE_INTERVAL=5;
	@Id
	@GeneratedValue
	private Long id;
	private String firstname;
	private String lastname;
	@Column(unique = true, nullable = false)
	private String email;
	private String password;
	private String profileImg;
	private LocalDateTime lastSeen;
	@Enumerated(EnumType.STRING)
	private Role role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}
	
	@Transient
	public boolean isUserOnline() {
		return lastSeen !=null && lastSeen.isAfter(LocalDateTime.now().plusMinutes(LAST_ACTIVE_INTERVAL));
	}
}
