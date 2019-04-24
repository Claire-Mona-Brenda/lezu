package com.konka.renting.bean;

import java.util.List;

public class AgentBean {
        /**
         * id : 100000001
         * agent_name : 广州代理
         * agent_code : 10228063
         */

        private String agent_id;
        private String agent_name;
        private String agent_code;

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_name() {
            return agent_name;
        }

        public void setAgent_name(String agent_name) {
            this.agent_name = agent_name;
        }

        public String getAgent_code() {
            return agent_code;
        }

        public void setAgent_code(String agent_code) {
            this.agent_code = agent_code;
        }

}
