package ru.practicum.explore.admin.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.users.dto.UserDto;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.admin.users.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody @Valid UserDto dto) {
        return service.addUser(dto);
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) List<Long> ids) {
        return service.getUsers(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}
