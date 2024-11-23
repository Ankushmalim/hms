package com.hms.controller;

import com.hms.entity.AppUser;
import com.hms.payload.LoginDto;
import com.hms.payload.TokenDto;
import com.hms.repository.AppUserRepository;
import com.hms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private AppUserRepository appUserRepository;
    private UserService userService;

    public UserController(AppUserRepository appUserRepository, UserService userService) {
        this.appUserRepository = appUserRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(
            @RequestBody AppUser user
    ){
        Optional<AppUser> opUsername = appUserRepository.findByUsername(user.getUsername());
        if(opUsername.isPresent()){
            return new ResponseEntity<>("userName already taken", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<AppUser> opEmail= appUserRepository.findByEmail(user.getEmail());
        if(opEmail.isPresent()){
            return new ResponseEntity<>("Email already Used", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //this is for encrypt password
        String encryptedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(5));
        user.setPassword(encryptedPassword);

        AppUser savedUser = appUserRepository.save(user);
        return new ResponseEntity<>("Save user", HttpStatus.CREATED);
    }

    @GetMapping("/message")
    public String getMessage(){
        return "hello";
    }

  @PostMapping("/login")
  public ResponseEntity<?> login(
        @RequestBody LoginDto dto
  ){
      String token = userService.verifyLogin(dto);
      if(token!=null){
          TokenDto tokenDto = new TokenDto();
          tokenDto.setToken(token);
          tokenDto.setType("JWT");
          return new ResponseEntity<>(tokenDto, HttpStatus.OK);
      }else{
          return new ResponseEntity<>("Invalid userName/password", HttpStatus.FORBIDDEN);
      }

  }
}
