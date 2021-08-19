package com.example.mobile.app.ws.io.repositories;

import com.example.mobile.app.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
//cao zuo database
@Repository
//public interface UserRepository extends CrudRepository<UserEntity, Long> {
//this is for the functionality of finding a list of users on page by limit
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    //UserEntity findUserByEmail(String email);
    //The following methods are provided by CrudRepository
    //convention: findBy +  SQL_data_field (capitalized)
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findByLastName(String lastName);
}
