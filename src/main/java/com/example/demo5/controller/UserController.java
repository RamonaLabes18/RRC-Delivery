package com.example.demo5.controller;


import com.example.demo5.dto.*;
import com.example.demo5.exceptions.AuthenticationFailException;
import com.example.demo5.exceptions.CustomException;
import com.example.demo5.models.AuthenticationToken;
import com.example.demo5.models.User;
import com.example.demo5.repository.UserRepository;
import com.example.demo5.service.AuthenticationService;
import com.example.demo5.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @GetMapping(value ="/all",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findAllUser() throws AuthenticationFailException {
        return userRepository.findAll();
    }

    @PostMapping(value = "/signUp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto Signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userService.signUp(signupDto);
    }

    @PostMapping(value = "/signIn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SignInResponseDto Signin(@RequestBody SignInDto signInDto) throws CustomException {
        return userService.signIn(signInDto);
    }

    //@PutMapping("/updateUser")
    @PutMapping(value = "/updateUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);
    }

    @DeleteMapping(value = "/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteManager(@PathVariable Integer id) {
        userService.deleteManager(id);
    }

    //create staff user like admin or manager
    //tokenul este cel al unui admin deoarece numai adminul poate crea un manager
    //contul de admin este deja facut la inceputul aplicatiei
    @PostMapping(value = "/createUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createUser(@RequestBody UserCreateDto userCreateDto)
            throws CustomException, AuthenticationFailException {
        User user = userRepository.findByEmail("admin@gmail.com");
        String token = authenticationService.getToken(user).toString();
        authenticationService.authenticate(token);
        return userService.createUser(token, userCreateDto);
    }
}

