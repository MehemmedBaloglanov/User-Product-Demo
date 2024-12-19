package az.codebridge.task.service.impl;

import az.codebridge.task.dto.UserRequestDto;
import az.codebridge.task.dto.UserResponseDto;
import az.codebridge.task.entity.UserEntity;
import az.codebridge.task.exception.UserNotFoundException;
import az.codebridge.task.repository.UserRepository;
import az.codebridge.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = UserEntity.builder()
                .userName(userRequestDto.getUserName())
                .name(userRequestDto.getName())
                .surname(userRequestDto.getSurname())
                .age(userRequestDto.getAge())
                .balance(userRequestDto.getBalance())
                .isActive(userRequestDto.getIsActive())
                .build();
        UserEntity save = userRepository.save(userEntity);
        return UserResponseDto.builder()
                .id(save.getId())
                .userName(save.getUserName())
                .name(save.getName())
                .surname(save.getSurname())
                .age(save.getAge())
                .balance(save.getBalance())
                .isActive(save.getIsActive())
                .build();
    }

//    @Override
//    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
//        UserEntity user = UserEntity.builder()
//                .id(id)
//                .userName(userRequestDto.getUserName())
//                .name(userRequestDto.getName())
//                .surname(userRequestDto.getSurname())
//                .age(userRequestDto.getAge())
//                .balance(userRequestDto.getBalance())
//                .isActive(userRequestDto.getIsActive())
//                .build();
//
//        UserEntity save = userRepository.save(user);
//
//        return UserResponseDto.builder()
//                .id(save.getId())
//                .userName(save.getUserName())
//                .name(save.getName())
//                .surname(save.getSurname())
//                .age(save.getAge())
//                .balance(save.getBalance())
//                .isActive(save.getIsActive())
//                .build();
//    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        UserEntity user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        user.setUserName(userRequestDto.getUserName());
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setAge(userRequestDto.getAge());
        user.setBalance(userRequestDto.getBalance());
        user.setIsActive(userRequestDto.getIsActive());

        UserEntity save = userRepository.save(user);

        return UserResponseDto.builder()
                .id(save.getId())
                .userName(save.getUserName())
                .name(save.getName())
                .surname(save.getSurname())
                .age(save.getAge())
                .balance(save.getBalance())
                .isActive(save.getIsActive())
                .build();
    }

    @Override
    public UserResponseDto getUser(Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .surname(user.getSurname())
                .age(user.getAge())
                .balance(user.getBalance())
                .isActive(user.getIsActive())
                .build();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto getUserByUserName(String userName) {
        UserEntity user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .surname(user.getSurname())
                .age(user.getAge())
                .balance(user.getBalance())
                .isActive(user.getIsActive())
                .build();
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userEntity ->
                        UserResponseDto.builder()
                                .id(userEntity.getId())
                                .userName(userEntity.getUserName())
                                .name(userEntity.getName())
                                .surname(userEntity.getSurname())
                                .age(userEntity.getAge())
                                .balance(userEntity.getBalance())
                                .isActive(userEntity.getIsActive())
                                .build()).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserBalanceWithUserName(String userName) {
        UserEntity user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .surname(user.getSurname())
                .age(user.getAge())
                .balance(user.getBalance())
                .isActive(user.getIsActive())
                .build();


    }


}
