package com.application.Application.mapper;

import com.application.Application.dto.UsersDTO;
import com.application.Application.entity.Users;
public class UsersMapper {
//
//
//        public static UsersDTO toDto(Users user) {
//            return new UsersDTO(
//                    user.getId(),
//                    user.getUsername(),
//                    user.getPassword(),
//                    user.getEmail(),
//                    user.getPhoneNumber(),
//                    user.isEmailVerified(),
//                    user.isPhoneNumberVerified(),
//                    user.getCountry(),
//                    user.getCreatedAt(),
//                    user.getUpdatedAt()
//            );
//        }
//
//        public static Users toEntity(UsersDTO userDto) {
//            Users user = new Users();
//            user.setId(userDto.getId());
//            user.setUsername(userDto.getUsername());
//            user.setPassword(userDto.getPassword());
//            user.setEmail(userDto.getEmail());
//            user.setPhoneNumber(userDto.getPhoneNumber());
//            user.setEmailVerified(userDto.isEmailVerified());
//            user.setPhoneNumberVerified(userDto.isPhoneNumberVerified());
//            user.setCountry(userDto.getCountry());
//            user.setCreatedAt(userDto.getCreatedAt());
//            user.setUpdatedAt(userDto.getUpdatedAt());
//
//
//
//            return user;
//        }


        public static UsersDTO toDto(Users user) {
            return new UsersDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.isEmailVerified(),
                    user.isPhoneNumberVerified(),
                    user.getRoles(),
                    user.getCountry(),
                    user.getPhoneOtp(),
                    user.getEmailOtp(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()// Map roles
            );
        }

        public static Users toEntity(UsersDTO userDto) {
            Users user = new Users();
            user.setId(userDto.getId());
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setEmailVerified(userDto.isEmailVerified());
            user.setPhoneNumberVerified(userDto.isPhoneNumberVerified());
            user.setCountry(userDto.getCountry());
            user.setPhoneOtp(userDto.getPhoneOtp());
            user.setEmailOtp(userDto.getEmailOtp());
            user.setCreatedAt(userDto.getCreatedAt());
            user.setUpdatedAt(userDto.getUpdatedAt());
            user.setRoles(userDto.getRoles()); // Map roles

            return user;
        }
    }



