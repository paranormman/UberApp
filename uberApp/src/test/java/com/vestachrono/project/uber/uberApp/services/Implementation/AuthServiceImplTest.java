package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.TestContainerConfiguration;
import com.vestachrono.project.uber.uberApp.dto.SignupDto;
import com.vestachrono.project.uber.uberApp.dto.UserDto;
import com.vestachrono.project.uber.uberApp.entities.User;
import com.vestachrono.project.uber.uberApp.entities.enums.Role;
import com.vestachrono.project.uber.uberApp.repositories.UserRepository;
import com.vestachrono.project.uber.uberApp.security.JWTService;
import com.vestachrono.project.uber.uberApp.services.DriverService;
import com.vestachrono.project.uber.uberApp.services.RiderService;
import com.vestachrono.project.uber.uberApp.services.WalletService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RiderServiceImpl riderServiceImpl;
    @Mock
    private WalletServiceImpl walletServiceImpl;
    @Mock
    private DriverServiceImpl driverServiceImpl;
    @Spy
    private ModelMapper modelMapper;
    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void SetUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));
    }

    @Test
    void testLogin_whenSuccess() {
//        assign
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

//        act
        String[] tokens = authService.login(user.getEmail(), user.getPassword());

//        assert
        assertThat(tokens).hasSize(2);
        assertThat(tokens[0]).isEqualTo("accessToken");
        assertThat(tokens[1]).isEqualTo("refreshToken");
    }

    @Test
    void testSignup_whenSuccess() {
//        assign
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

//        act
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("test@example.com");
        signupDto.setPassword("password");
        UserDto userDto = authService.signup(signupDto);

//        assert
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(signupDto.getEmail());

        verify(riderServiceImpl).createNewRider(any(User.class));
        verify(walletServiceImpl).createNewWallet(any(User.class));
    }

}