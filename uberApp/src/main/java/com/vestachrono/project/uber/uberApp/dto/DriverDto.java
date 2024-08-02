package com.vestachrono.project.uber.uberApp.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private UserDto user;
    private double rating;

}
