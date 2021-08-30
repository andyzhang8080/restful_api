package com.example.mobile.app.ws.ui.controller;

import com.example.mobile.app.ws.exceptions.UserServiceException;
import com.example.mobile.app.ws.service.AddressService;
import com.example.mobile.app.ws.service.UserService;
import com.example.mobile.app.ws.shared.dto.AddressDTO;
import com.example.mobile.app.ws.shared.dto.UserDto;
import com.example.mobile.app.ws.ui.model.request.UserDetailsRequestModel;
import com.example.mobile.app.ws.ui.model.response.*;
import org.apache.catalina.User;
import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/mobile-app-ws/users
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    AddressService addressesService;

    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest putUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }


    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    //order matter. The first media type is the default
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        //BeanUtils.copyProperties(userDto, returnValue);
        returnValue = new ModelMapper().map(userDto, UserRest.class);
        return returnValue;
    }

    //http://localhost:8080/mobile-app-ws/users/jsfjglajg/addresses
    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    //order matter. The first media type is the default
//    public List<AddressesRest> getUserAddresses(@PathVariable String id) {
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> returnValue = new ArrayList<>();
        List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
        if(addressesDTO != null && !addressesDTO.isEmpty()) {
            //in the next line, we need to write List<AddressesRest> because it does not contain user details such as password
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();

            returnValue = new ModelMapper().map(addressesDTO, listType);

            for(AddressesRest addressesRest : returnValue) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddress(id, addressesRest.getAddressId()))
                        .withSelfRel();
                addressesRest.add(selfLink);
            }
        }
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    //order matter. The first media type is the default

    //public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDTO = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);

        //one method using representation model
        /*
        //http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        //http://localhost:8080/users/<userId>/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .slash("addresses")
                .withRel("addresses");
        //http://localhost:8080/users/<userId>/addresses/<addressId>
        Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .slash("addresses")
                .slash(addressId)
                .withSelfRel();

         */

        Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId))
                .withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        /*
        //return AddressRest with extention to Representation model
        returnValue.add(userLink);
        returnValue.add(userAddressesLink);
        returnValue.add(selfLink);
        return returnValue;
         */

        //another way
        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }


    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        //throw new NullPointerException(); //only a test for exceptions handler
        //UserDto userDto = new UserDto();
        //BeanUtils.copyProperties(userDetails, userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        //BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);

        for(UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }


}