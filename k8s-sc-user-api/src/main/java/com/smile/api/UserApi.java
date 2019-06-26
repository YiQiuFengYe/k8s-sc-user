package com.smile.api;

import com.smile.model.qo.UserListQo;
import com.smile.model.vo.UserVo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-user")
public interface UserApi {

    @GetMapping(value = "/list")
    List<UserVo> list(@RequestParam(required = false, value = "name") String name,
        @RequestParam(required = false, value = "age") Integer age);

}
