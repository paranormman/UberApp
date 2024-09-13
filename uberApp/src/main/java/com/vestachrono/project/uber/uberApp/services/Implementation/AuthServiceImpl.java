package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.LoginResponseDto;
import com.vestachrono.project.uber.uberApp.dto.SignupDto;
import com.vestachrono.project.uber.uberApp.dto.UserDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.User;
import com.vestachrono.project.uber.uberApp.entities.enums.Role;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.vestachrono.project.uber.uberApp.repositories.UserRepository;
import com.vestachrono.project.uber.uberApp.security.JWTService;
import com.vestachrono.project.uber.uberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.vestachrono.project.uber.uberApp.entities.enums.Role.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    @Override
    public String[] login(String email, String password) {
//        sending the user details for the filter chain to authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
//        get the authenticated user
        User user = (User) authentication.getPrincipal();

//        create an accessToken for the authenticated user
        String accessToken = jwtService.generateAccessToken(user);
//        create refreshToken for the authenticated user
        String refreshToken = jwtService.generateRefreshToken(user);

//        return both accessToken and refreshToken
        return new String[] {accessToken, refreshToken};
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeConflictException("Can not SignUp, User email already exists " + signupDto.getEmail());
        }

        User mappedUser = modelMapper.map(signupDto, User.class);
//        Set the roles to the User
        mappedUser.setRoles(Set.of(RIDER));
//        encode the password before saving the user
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
//        save the user in the repo
        User savedUser = userRepository.save(mappedUser);

//        Create user related entities
        riderService.createNewRider(savedUser);

//        Create a new wallet for the user
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId, String vehicleId) {
//        get the user from repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+userId));
//        check if the user is already a driver
        if (user.getRoles().contains(DRIVER))
            throw new RuntimeConflictException("User with id "+userId+" is already a Driver");
//        create a driver entity
//        we use a builder method when we need to create any object with the details from entity with the user details
        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .available(true)
                .vehicleId(vehicleId)
                .build();
//        update the (driver)role to the user and save it
        user.getRoles().add(DRIVER);
        userRepository.save(user);
//        save the driver to repository
        Driver savedDriver = driverService.createNewDriver(createDriver);
//        return the saved driver as Dto
        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public String refreshToken(String refreshToken) {
//        get userId from the refreshToken
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository
                .findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Id:" + userId));

       return jwtService.generateAccessToken(user);
    }
}
