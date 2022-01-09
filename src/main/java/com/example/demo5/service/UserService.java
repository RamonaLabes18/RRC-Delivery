package com.example.demo5.service;

import com.example.demo5.dto.*;
import com.example.demo5.enums.ResponseStatus;
import com.example.demo5.enums.UserRole;
import com.example.demo5.exceptions.AuthenticationFailException;
import com.example.demo5.exceptions.CustomException;
import com.example.demo5.models.AuthenticationToken;
import com.example.demo5.models.User;
import com.example.demo5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public
class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostConstruct
    private
    void createAdmin() {
        String email = "admin@gmail.com";
        if (!userRepository.existsByEmail(email)) {
            UserCreateDto user = new UserCreateDto();
            user.setFirstName("admin");
            user.setLastName("admin");
            user.setEmail("admin@gmail.com");
            user.setAddress("Brasov, Saturn");
            user.setHouseNumber(4);
            user.setPassword("administrator");
            user.setUserRole(UserRole.admin);
            createAdmin(user);
        }
    }

    //this method is used when client want to make an account
    public
    ResponseDto signUp(SignupDto signupDto) throws CustomException {
        // Check to see if the current email address has already been registered.
        if (userRepository.findByEmail(signupDto.getEmail()) != null) {
            // If the email address has been registered then throw an exception.
            throw new CustomException("User already exists");
        }
        // first encrypt the password
        String encryptedPassword = signupDto.getPassword();
        try {
            encryptedPassword = hashPassword(signupDto.getPassword());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }


        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), UserRole.user, encryptedPassword, signupDto.getAddress(), signupDto.getHouseNumber());

        User createdUser;
        try {
            // save the User
            createdUser = userRepository.save(user);
            // generate token for user
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            // save token in database
            authenticationService.saveConfirmationToken(authenticationToken);
            // success in creating
            return new ResponseDto(ResponseStatus.success.toString(), "USER_CREATED");
        }
        catch (Exception e) {
            // handle signup error
            throw new CustomException(e.getMessage());
        }
    }

    //this method is used by client to sign in
    public
    SignInResponseDto signIn(SignInDto signInDto) throws CustomException {
        // first find User by email
        User user = userRepository.findByEmail(signInDto.getEmail());
        if (user == null) {
            throw new AuthenticationFailException("user not present");
        }
        try {
            // check if password is right
            if (!user.getPassword().equals(hashPassword(signInDto.getPassword()))) {
                // passowrd doesnot match
                throw new AuthenticationFailException("WRONG_PASSWORD");
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        AuthenticationToken token = authenticationService.getToken(user);
        //return new SignInResponseDto ("success", token.getToken());
        return new SignInResponseDto("success", token.getToken());
    }

    //encrypted the password
    String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myHash;
    }

    //this method is used when the admin want to create new manager or admin
    public
    ResponseDto createUser(String token, UserCreateDto userCreateDto) throws CustomException, AuthenticationFailException {
        User creatingUser = authenticationService.getUser(token);

        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new AuthenticationFailException("Email_exist");
        }
        String encryptedPassword = userCreateDto.getPassword();
        try {
            encryptedPassword = hashPassword(userCreateDto.getPassword());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }
        User user = new User(userCreateDto.getFirstName(), userCreateDto.getLastName(), userCreateDto.getEmail(), userCreateDto.getUserRole(), encryptedPassword, userCreateDto.getAddress(), userCreateDto.getHouseNumber());
        User createdUser;
        try {
            createdUser = userRepository.save(user);
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            authenticationService.saveConfirmationToken(authenticationToken);
            return new ResponseDto(ResponseStatus.success.toString());
        }
        catch (Exception e) {
            // handle user creation fail error
            throw new CustomException(e.getMessage());
        }

    }

    //just for create admin when the database is empty
    public
    ResponseDto createAdmin(UserCreateDto userCreateDto) throws CustomException, AuthenticationFailException {
        String encryptedPassword = userCreateDto.getPassword();
        try {
            encryptedPassword = hashPassword(userCreateDto.getPassword());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }

        User user = new User(userCreateDto.getFirstName(), userCreateDto.getLastName(), userCreateDto.getEmail(), userCreateDto.getUserRole(), encryptedPassword, userCreateDto.getAddress(), userCreateDto.getHouseNumber());
        User createdUser;
        try {
            createdUser = userRepository.save(user);
            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
            authenticationService.saveConfirmationToken(authenticationToken);
            return new ResponseDto(ResponseStatus.success.toString(), "USER_CREATED");
        }
        catch (Exception e) {
            // handle user creation fail error
            throw new CustomException(e.getMessage());
        }
    }

    //this method is used when the client want to update his profile
    public
    void updateUser(UserUpdateDto userUpdateDto) {
        String email = userUpdateDto.getEmail();
        User user = userRepository.findByEmail(email);
        user.setHouseNumber(userUpdateDto.getHouseNumber());
        user.setLastName(userUpdateDto.getLastName());
        user.setAddress(userUpdateDto.getAddress());
        user.setFirstName(userUpdateDto.getAddress());
        userRepository.save(user);
    }

    //when admin want to delete a manager
    public void deleteManager(Integer id) {
        userRepository.deleteById(id);
    }

//
//    boolean canCrudUser(UserRole role) {
//        if (role == UserRole.admin || role == UserRole.manager) {
//            return true;
//        }
//        return false;
//    }
//
//    boolean canCrudUser(User userUpdating, Integer userIdBeingUpdated) {
//        UserRole role   = userUpdating.getRole();
//        boolean  result = false;
//        // admin and manager can crud any user
//        if (role == UserRole.admin || role == UserRole.manager) {
//            result = true;
//        }
//        // user can update his own record, but not his role
//        if (role == UserRole.user && userUpdating.getId() == userIdBeingUpdated) {
//            result = true;
//        }
//        return result;
//    }

}

