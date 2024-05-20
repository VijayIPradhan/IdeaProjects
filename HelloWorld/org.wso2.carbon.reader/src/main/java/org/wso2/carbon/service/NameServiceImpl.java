package org.wso2.carbon.service;

import org.wso2.carbon.Name;

public class NameServiceImpl implements NameService {
    @Override
    public String getName(Name name) {
        return name.getName()+" "+name.getAge();
    }
}
