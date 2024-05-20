package org.wso2.carbon.activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        try {
            // Create and start the OSGi framework
            FrameworkFactory factory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
            Map<String, String> config = new HashMap<>();
            config.put(Constants.FRAMEWORK_STORAGE, "./osgi-storage");
            config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
            Framework framework = factory.newFramework(config);
            framework.start();

            // Get the BundleContext
            BundleContext bundleContext = framework.getBundleContext();

            new HelloWorld().start(bundleContext);

                Bundle bundle1 = bundleContext.installBundle("file:C:\\Users\\Allapu.Srikanth\\IdeaProjects\\HelloWorld\\Greeter\\target\\Greeter-1.0-SNAPSHOT.jar");
                Bundle bundle = bundleContext.installBundle("file:C:\\Users\\Allapu.Srikanth\\IdeaProjects\\HelloWorld\\org.wso2.carbon.reader\\target\\Reader-1.0-SNAPSHOT.jar");
                bundle.start();
                bundle1.start();
                framework.waitForStop(5000);
                framework.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
