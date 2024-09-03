package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.SignupDto;
import com.vestachrono.project.uber.uberApp.dto.UserDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.User;
import com.vestachrono.project.uber.uberApp.entities.enums.Role;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.vestachrono.project.uber.uberApp.repositories.UserRepository;
import com.vestachrono.project.uber.uberApp.services.AuthService;
import com.vestachrono.project.uber.uberApp.services.DriverService;
import com.vestachrono.project.uber.uberApp.services.RiderService;
import com.vestachrono.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.vestachrono.project.uber.uberApp.entities.enums.Role.DRIVER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeConflictException("Can not SignUp, User email already exists " + signupDto.getEmail());
        }

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
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
}
