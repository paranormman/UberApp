package com.vestachrono.project.uber.uberApp.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class MapperConfig {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
