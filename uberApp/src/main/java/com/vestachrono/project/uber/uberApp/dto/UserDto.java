package com.vestachrono.project.uber.uberApp.dto;

import com.vestachrono.project.uber.uberApp.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private Set<Role> roles;
}
