package com.auth.AuthImpl.registraion.mapper;
import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;

public class UsersMapper {


    public static Users dtoToEntity(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new IllegalArgumentException("UserRequestDto cannot be null");
        }

        Users user = new Users();
        user.setId(userRequestDto.getId());
        user.setUsername(getUsernameFromRequest(userRequestDto,user.getId()));
        user.setEmail(userRequestDto.getEmail());
        user.setIsdCode(userRequestDto.getIsdCode());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        return user;
    }



    private static String getUsernameFromRequest(UserRequestDto userRequestDto, Long userId) {
        if (userRequestDto.getPhoneNumber() != null && !userRequestDto.getPhoneNumber().isEmpty()) {
            return "user" + userId; // Generate username based on userId
        } else if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()) {
            return userRequestDto.getEmail().substring(0, userRequestDto.getEmail().indexOf("@")); // Extract part before '@'
        }
        throw new IllegalArgumentException("Either phone number or email must be provided.");
    }


    public static UsersResponseDTO mapToDTO(Users user, String message) {
        if (user == null) {
            throw new IllegalArgumentException("Users entity cannot be null");
        }

        UsersResponseDTO responseDTO = new UsersResponseDTO(user,message);
        return responseDTO;
    }

    public static UsersResponseDTO mapToUserRequestDto(UserRequestDto user) {
        if (user == null) {
            throw new IllegalArgumentException("Users entity cannot be null");
        }

        UsersResponseDTO responseDTO = new UsersResponseDTO(user);
        return responseDTO;
    }


}
