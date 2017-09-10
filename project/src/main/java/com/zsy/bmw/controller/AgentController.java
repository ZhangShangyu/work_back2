package com.zsy.bmw.controller;


import com.zsy.bmw.model.Agent;
import com.zsy.bmw.service.AgentService;
import com.zsy.bmw.utils.Constant;
import com.zsy.bmw.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @RequestMapping("login")
    public Result login(@RequestBody Agent agent) {
        agent = agentService.checkLogin(agent);
        if (agent != null) {
            Result result = new Result(Constant.OK_CODE, Constant.OK);
            result.setData(agent.getId());
            return result;
        }
        return new Result(Constant.ERROR_CODE1, Constant.LOGIN_ERROR);
    }
}
