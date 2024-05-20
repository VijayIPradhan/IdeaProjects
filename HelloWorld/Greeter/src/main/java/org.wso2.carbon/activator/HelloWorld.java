package org.wso2.carbon.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.carbon.Name;
import org.wso2.carbon.service.NameService;
import org.wso2.carbon.service.NameServiceImpl;


public class HelloWorld implements BundleActivator {
    private final Name name = new Name("Java", 22);
    NameService nameService=new NameServiceImpl();


    public void start(BundleContext ctx) throws Exception {
        try {
            System.out.println("Hello " + nameService.getName(name));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void stop(BundleContext bundleContext) {
        System.out.println("Good Bye " + nameService.getName(name));
    }

}

