package com.spring.starter.api.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.starter.api.request.user.LoginReq;
import com.spring.starter.api.request.user.SingUpUserReq;
import com.spring.starter.api.response.index.LoginRes;
import com.spring.starter.api.service.AreaService;
import com.spring.starter.api.service.JwtService;
import com.spring.starter.api.service.UserService;
import com.spring.starter.common.model.BaseResponse;
import com.spring.starter.db.entity.Area;
import com.spring.starter.db.entity.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final AreaService areaService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@PostMapping("/signup")
	public ResponseEntity<? extends BaseResponse> signUpUser(@Valid @RequestBody SingUpUserReq singUpUserReq) {
		if (userService.isExistEmail(singUpUserReq.getEmail()))
			// 200, 201, 400, 401, 403, 409, 500
			return ResponseEntity.status(409).body(new BaseResponse("이미 존재하는 이메일입니다.", 409));
		if (userService.isExistUserId(singUpUserReq.getUserId()))
			return ResponseEntity.status(409).body(new BaseResponse("이미 존재하는 아이디입니다.", 409));

		Area area = null;
		if (singUpUserReq.getArea() != null) {
			area = areaService.nameToArea(singUpUserReq.getArea());
			if (area == null)
				return ResponseEntity.status(400).body(new BaseResponse("Area의 값이 잘못됐습니다.", 400));
		}

		// ToDo(천천히) 학생 Area -> 자동화
		String encode = passwordEncoder.encode(singUpUserReq.getPassword());
		userService.save(singUpUserReq.toUserEntity(area, encode));

		return ResponseEntity.status(201).body(new BaseResponse("회원가입에 성공하였습니다.", 201));
	}

	@PostMapping("/login")
	public ResponseEntity<? extends BaseResponse> login(@Valid @RequestBody LoginReq loginReq) {
		User byId = userService.findById(loginReq.getUserId());

		if (byId == null) {
			return ResponseEntity.status(400).body(new BaseResponse("존재하지 않는 아이디입니다.", 400));
		}

		if (!passwordEncoder.matches(loginReq.getPassword(), byId.getPassword())) {
			return ResponseEntity.status(400).body(new BaseResponse("비밀번호가 일치하지 않습니다.", 400));
		}

		// ToDo Auth 테이블과 인터셉터 관리
		String accessToken = jwtService.generateJwtToken(byId);
		String refreshToken = jwtService.saveRefreshToken(byId);
		return ResponseEntity.status(201).body(new LoginRes("로그인을 성공적으로 했습니다.", 201, accessToken, refreshToken));
	}

}
