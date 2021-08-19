package com.example.mobile.app.ws.service.impl;

import com.example.mobile.app.ws.io.entity.AddressEntity;
import com.example.mobile.app.ws.io.entity.UserEntity;
import com.example.mobile.app.ws.io.repositories.AddressRepository;
import com.example.mobile.app.ws.io.repositories.UserRepository;
import com.example.mobile.app.ws.service.AddressService;
import com.example.mobile.app.ws.shared.dto.AddressDTO;
import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null)  return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for(AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
        }

        return returnValue;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (addressEntity != null) {
            return new ModelMapper().map(addressEntity, AddressDTO.class);
        } else {
            return null;
        }
    }
}
