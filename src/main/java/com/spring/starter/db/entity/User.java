package com.spring.starter.db.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.spring.starter.api.request.user.UpdateInfoReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
	@NotNull
	@Column(unique = true)
	String userId;

	@NotNull
	String name;

	@NotNull
	@Column(unique = true)
	String email;

	@NotNull
	String password;

	@NotNull
	Integer studentId;

//	ROLE_USER, ROLE_MENTOR, ROLE_ADMIN
	@Column(name = "user_role")
	@Enumerated(EnumType.STRING)
	private Authority authority;

	@ManyToOne
	@JoinColumn
	Area area;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	List<AMAM> amamList;
	public void addAMAM(AMAM amam) {
		if (this.amamList == null) {
			this.amamList = new LinkedList<>();
		}
		this.amamList.add(amam);
		amam.setUser(this);
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void setInfo(UpdateInfoReq updateInfoReq, Area area) {
		this.name = updateInfoReq.getName();
		this.password = updateInfoReq.getPassword();
		this.studentId = updateInfoReq.getStudentId();
		this.email = updateInfoReq.getEmail();
		this.area = area;
	}
}
