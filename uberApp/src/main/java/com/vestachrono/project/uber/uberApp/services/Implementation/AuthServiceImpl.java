package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.SignupDto;
import com.vestachrono.project.uber.uberApp.dto.UserDto;
import com.vestachrono.project.uber.uberApp.entities.Rider;
import com.vestachrono.project.uber.uberApp.entities.User;
import com.vestachrono.project.uber.uberApp.entities.enums.Role;
import com.vestachrono.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.vestachrono.project.uber.uberApp.repositories.UserRepository;
import com.vestachrono.project.uber.uberApp.services.AuthService;
import com.vestachrono.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeConflictException("Can not SignUp, User email already exists " + signupDto.getEmail());
        }

        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDERS));
        User savedUser = userRepository.save(mappedUser);

//        Create user related entities
        riderService.CreateNewRider(savedUser);

//        TODO add Wallet related service here

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
