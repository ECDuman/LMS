package com.demo.lms.service;

import com.demo.lms.dto.CreateUserRequest;
import com.demo.lms.dto.UpdateUserRequest;
import com.demo.lms.dto.UserResponse;
import com.demo.lms.exception_handling.ResourceNotFoundException;
import com.demo.lms.mapper.UserMapper;
import com.demo.lms.model.Organization;
import com.demo.lms.model.ProfileType;
import com.demo.lms.model.RoleEnum;
import com.demo.lms.model.User;
import com.demo.lms.repository.OrganizationRepository;
import com.demo.lms.repository.ProfileTypeRepository;
import com.demo.lms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLoggerService auditLoggerService;

    @Mock
    private PermissionCheckerService permissionCheckerService;

    @Mock
    private ProfileTypeRepository profileTypeRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UUID organizationId;
    private int profileType;
    private User user;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;
    private ProfileType studentProfileType;
    private ProfileType teacherProfileType;
    private Organization organization;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        profileType = 1;

        organization = new Organization();
        organization.setId(organizationId);

        studentProfileType = new ProfileType();
        studentProfileType.setProfileId(profileType);
        studentProfileType.setName(RoleEnum.STUDENT); // RoleEnum ile güncellendi

        teacherProfileType = new ProfileType();
        teacherProfileType.setProfileId(2);
        teacherProfileType.setName(RoleEnum.TEACHER); // RoleEnum ile güncellendi

        user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setProfileType(studentProfileType);
        user.setOrganization(organization);

        createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("New User");
        createUserRequest.setEmail("new.user@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setOrganizationId(organizationId);
        createUserRequest.setProfileType(profileType);

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("Updated Name");
        updateUserRequest.setEmail("updated.email@example.com");
        updateUserRequest.setPassword("newpassword");

        userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setFirstName("John");
        userResponse.setEmail("john.doe@example.com");
    }


    @Nested
    @DisplayName("Read Operations")
    class ReadOperations {

        @Test
        @DisplayName("All users should return successfully")
        void shouldGetAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(user));
            when(userMapper.toResponseList(any())).thenReturn(List.of(userResponse));

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            verify(userRepository).findAll();
            verify(userMapper).toResponseList(any());
        }

        @Test
        @DisplayName("All teachers must return successfully")
        void shouldGetAllTeachers() {
            User teacher = new User();
            teacher.setProfileType(teacherProfileType);
            when(userRepository.findAll()).thenReturn(List.of(user, teacher));
            when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse());

            List<UserResponse> result = userService.getAllTeachers();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("All students must return successfully")
        void shouldGetAllStudents() {
            User teacher = new User();
            teacher.setProfileType(teacherProfileType);
            when(userRepository.findAll()).thenReturn(List.of(user, teacher));
            when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse());

            List<UserResponse> result = userService.getAllStudents();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("If the ID is present, it should return the user successfully.")
        void shouldGetUserByIdWhenExists() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

            UserResponse result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("If the ID does not exist, throw ResourceNotFoundException")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(UUID.randomUUID()));
            verify(userRepository).findById(any(UUID.class));
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    @DisplayName("Create User")
    class CreateUser {

        @Test
        @DisplayName("If authorized, the user should be created successfully")
        void shouldCreateUserSuccessfullyWithPermission() {
            when(permissionCheckerService.hasPermission("User", "CREATE")).thenReturn(true);
            when(profileTypeRepository.findById(profileType)).thenReturn(Optional.of(studentProfileType));
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(userMapper.toEntity(any(CreateUserRequest.class), any(ProfileType.class), any(Organization.class))).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

            UserResponse result = userService.createUser(createUserRequest);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            verify(permissionCheckerService).hasPermission("User", "CREATE");
            verify(profileTypeRepository).findById(profileType);
            verify(organizationRepository).findById(organizationId);
            verify(userMapper).toEntity(any(CreateUserRequest.class), any(ProfileType.class), any(Organization.class));
            verify(passwordEncoder).encode(anyString());
            verify(userRepository).save(any(User.class));
            verify(auditLoggerService).log("CREATE", "User", userId);
        }

        @Test
        @DisplayName("If no authorization is provided, throw AccessDeniedException")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            when(permissionCheckerService.hasPermission("User", "CREATE")).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> userService.createUser(createUserRequest));
            verify(permissionCheckerService).hasPermission("User", "CREATE");
            verifyNoInteractions(userRepository, userMapper, passwordEncoder, auditLoggerService, profileTypeRepository, organizationRepository);
        }

        @Test
        @DisplayName("If the profile type is not found, throw ResourceNotFoundException")
        void shouldThrowExceptionWhenProfileTypeNotFound() {
            when(permissionCheckerService.hasPermission("User", "CREATE")).thenReturn(true);
            when(profileTypeRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.createUser(createUserRequest));
            verify(permissionCheckerService).hasPermission("User", "CREATE");
            verify(profileTypeRepository).findById(any(Integer.class));
            verifyNoInteractions(organizationRepository, userRepository, userMapper, passwordEncoder, auditLoggerService);
        }

        @Test
        @DisplayName("If the organization is not found, it should throw a ResourceNotFoundException")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            when(permissionCheckerService.hasPermission("User", "CREATE")).thenReturn(true);
            when(profileTypeRepository.findById(profileType)).thenReturn(Optional.of(studentProfileType));
            when(organizationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.createUser(createUserRequest));
            verify(permissionCheckerService).hasPermission("User", "CREATE");
            verify(profileTypeRepository).findById(profileType);
            verify(organizationRepository).findById(any(UUID.class));
            verifyNoInteractions(userRepository, userMapper, passwordEncoder, auditLoggerService);
        }
    }


    @Nested
    @DisplayName("User Update")
    class UpdateUser {

        @Test
        @DisplayName("If authorized, update the user successfully")
        void shouldUpdateUserSuccessfullyWithPermission() {
            when(permissionCheckerService.hasPermission("User", "UPDATE")).thenReturn(true);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

            UserResponse result = userService.updateUser(userId, updateUserRequest);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            verify(permissionCheckerService).hasPermission("User", "UPDATE");
            verify(userRepository).findById(userId);
            verify(userMapper).updateEntity(updateUserRequest, user);
            verify(passwordEncoder).encode(updateUserRequest.getPassword());
            verify(userRepository).save(any(User.class));
            verify(auditLoggerService).log("UPDATE", "User", userId);
        }

        @Test
        @DisplayName("If no authorization is provided, throw AccessDeniedException")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            when(permissionCheckerService.hasPermission("User", "UPDATE")).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, updateUserRequest));
            verify(permissionCheckerService).hasPermission("User", "UPDATE");
            verifyNoInteractions(userRepository, userMapper, passwordEncoder, auditLoggerService, profileTypeRepository, organizationRepository);
        }

        @Test
        @DisplayName("If the user is not found, throw a ResourceNotFoundException")
        void shouldThrowResourceNotFoundExceptionIfUserNotFound() {
            when(permissionCheckerService.hasPermission("User", "UPDATE")).thenReturn(true);
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(UUID.randomUUID(), updateUserRequest));
            verify(permissionCheckerService).hasPermission("User", "UPDATE");
            verify(userRepository).findById(any(UUID.class));
            verifyNoInteractions(userMapper, passwordEncoder, auditLoggerService);
        }
    }


    @Nested
    @DisplayName("User Deletion")
    class DeleteUser {

        @Test
        @DisplayName("If authorized, the user should be deleted successfully")
        void shouldDeleteUserSuccessfullyWithPermission() {
            when(permissionCheckerService.hasPermission("User", "DELETE")).thenReturn(true);
            when(userRepository.existsById(userId)).thenReturn(true);

            userService.deleteUser(userId);

            verify(permissionCheckerService).hasPermission("User", "DELETE");
            verify(userRepository).existsById(userId);
            verify(userRepository).deleteById(userId);
            verify(auditLoggerService).log("DELETE", "User", userId);
        }

        @Test
        @DisplayName("If no authorization is provided, throw AccessDeniedException")
        void shouldThrowAccessDeniedExceptionIfNoPermission() {
            when(permissionCheckerService.hasPermission("User", "DELETE")).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> userService.deleteUser(userId));
            verify(permissionCheckerService).hasPermission("User", "DELETE");
            verifyNoInteractions(userRepository, auditLoggerService);
        }

        @Test
        @DisplayName("If the user is not found, throw a ResourceNotFoundException")
        void shouldThrowResourceNotFoundExceptionIfUserNotFound() {
            when(permissionCheckerService.hasPermission("User", "DELETE")).thenReturn(true);
            when(userRepository.existsById(any(UUID.class))).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
            verify(permissionCheckerService).hasPermission("User", "DELETE");
            verify(userRepository).existsById(any(UUID.class));
            verifyNoInteractions(auditLoggerService);
        }
    }
}