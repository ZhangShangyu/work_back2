package com.zsy.bmw.dao;

import com.zsy.bmw.model.Agent;


public interface AgentMapper {


    Agent getAgentByNameAndPassword(Agent agent);
}
