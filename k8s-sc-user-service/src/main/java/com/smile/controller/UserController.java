package com.smile.controller;

import com.smile.api.UserApi;
import com.smile.model.qo.UserListQo;
import com.smile.model.vo.UserVo;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    @Override
    public List<UserVo> list(String name, Integer age) {
        List<UserVo> result = Arrays.asList(1, 2, 3, 4, 5, 6, 7).stream()
            .map(value -> new UserVo(RandomStringUtils.randomAlphabetic(5), value))
            .collect(Collectors.toList());
        return result;
    }
}
