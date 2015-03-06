package com.interview.design.pattern.j2ee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午6:23
 *
 * The service locator design pattern is used when we want to locate various services using JNDI lookup.
 * Considering high cost of looking up JNDI for a service, Service Locator pattern makes use of caching technique.
 * For the first time a service is required, Service Locator looks up in JNDI and caches the service object.
 * Further lookup or same service via Service Locator is done in its cache which improves the performance of application to great extent.
 *
 * Following are the entities of this type of design pattern.
 * Service - Actual Service which will process the request. Reference of such service is to be looked upon in JNDI server.
 * Context / Initial Context -JNDI Context, carries the reference to service used for lookup purpose.
 * Service Locator - Service Locator is a single point of contact to get services by JNDI lookup, caching the services.
 * Cache - Cache to store references of services to reuse them
 * Client - Client is the object who invokes the services via ServiceLocator.
 */
public class ServiceLocatorPattern {

    static interface Service {
        public String getName();
        public void execute();
    }

    static class Service1 implements Service {
        public void execute(){
            System.out.println("Executing Service1");
        }

        @Override
        public String getName() {
            return "Service1";
        }
    }

    static class Service2 implements Service {
        public void execute(){
            System.out.println("Executing Service2");
        }

        @Override
        public String getName() {
            return "Service2";
        }
    }

    static class InitialContext {
        public Object lookup(String jndiName){
            if(jndiName.equalsIgnoreCase("SERVICE1")){
                System.out.println("Looking up and creating a new Service1 object");
                return new Service1();
            }else if (jndiName.equalsIgnoreCase("SERVICE2")){
                System.out.println("Looking up and creating a new Service2 object");
                return new Service2();
            }
            return null;
        }
    }

    static class Cache {

        private List<Service> services;

        public Cache(){
            services = new ArrayList<Service>();
        }

        public Service getService(String serviceName){
            for (Service service : services) {
                if(service.getName().equalsIgnoreCase(serviceName)){
                    System.out.println("Returning cached  "+serviceName+" object");
                    return service;
                }
            }
            return null;
        }

        public void addService(Service newService){
            boolean exists = false;
            for (Service service : services) {
                if(service.getName().equalsIgnoreCase(newService.getName())){
                    exists = true;
                }
            }
            if(!exists){
                services.add(newService);
            }
        }
    }

    static class ServiceLocator {
        private static Cache cache;

        static {
            cache = new Cache();
        }

        public static Service getService(String jndiName){

            Service service = cache.getService(jndiName);

            if(service != null){
                return service;
            }

            InitialContext context = new InitialContext();
            Service service1 = (Service)context.lookup(jndiName);
            cache.addService(service1);
            return service1;
        }
    }

    public static void main(String[] args) {
        Service service = ServiceLocator.getService("Service1");
        service.execute();
        service = ServiceLocator.getService("Service2");
        service.execute();
        service = ServiceLocator.getService("Service1");
        service.execute();
        service = ServiceLocator.getService("Service2");
        service.execute();
    }
}
