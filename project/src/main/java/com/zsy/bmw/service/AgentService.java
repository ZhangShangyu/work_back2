package com.zsy.bmw.service;

import com.zsy.bmw.dao.AgentMapper;
import com.zsy.bmw.model.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    @Autowired
    private AgentMapper agentMapper;

    public Agent checkLogin(Agent agent) {
        return agentMapper.getAgentByNameAndPassword(agent);
    }
}
