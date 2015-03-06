package com.interview.design.pattern.j2ee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午6:23
 *
 * The intercepting filter design pattern is used when we want to do some pre-processing / post-processing
 * with request or response of the application. Filters are defined and applied on the request before passing
 * the request to actual target application.
 * Filters can do the authentication/ authorization/ logging or tracking of request and then pass the requests
 * to corresponding handlers.
 *
 * Following are the entities of this type of design pattern.
 * Filter - Filter which will perform certain task prior or after execution of request by request handler.
 * Filter Chain - Filter Chain carries multiple filters and help to execute them in defined order on target.
 * Target - Target object is the request handler
 * Filter Manager - Filter Manager manages the filters and Filter Chain.
 * Client - Client is the object who sends request to the Target object.
 */
public class InterceptingFilterPattern {
    static interface Filter {
        public void execute(String request);
    }

    static class AuthenticationFilter implements Filter {
        public void execute(String request){
            System.out.println("Authenticating request: " + request);
        }
    }

    static class DebugFilter implements Filter {
        public void execute(String request){
            System.out.println("request log: " + request);
        }
    }

    static class Target {
        public void execute(String request){
            System.out.println("Executing request: " + request);
        }
    }

    static class FilterChain {
        private List<Filter> filters = new ArrayList<Filter>();
        private Target target;

        public void addFilter(Filter filter){
            filters.add(filter);
        }

        public void execute(String request){
            for (Filter filter : filters) {
                filter.execute(request);
            }
            target.execute(request);
        }

        public void setTarget(Target target){
            this.target = target;
        }
    }

    static class FilterManager {
        FilterChain filterChain;

        public FilterManager(Target target){
            filterChain = new FilterChain();
            filterChain.setTarget(target);
        }
        public void setFilter(Filter filter){
            filterChain.addFilter(filter);
        }

        public void filterRequest(String request){
            filterChain.execute(request);
        }
    }

    static class Client {
        FilterManager filterManager;

        public void setFilterManager(FilterManager filterManager){
            this.filterManager = filterManager;
        }

        public void sendRequest(String request){
            filterManager.filterRequest(request);
        }
    }

    public static void main(String[] args) {
        FilterManager filterManager = new FilterManager(new Target());
        filterManager.setFilter(new AuthenticationFilter());
        filterManager.setFilter(new DebugFilter());

        Client client = new Client();
        client.setFilterManager(filterManager);
        client.sendRequest("HOME");
    }
}
