package org.example.cacheengine;

import org.example.CacheService;
import org.example.TTLCache;

public class RateLimiter {
    private TTLCache<String,Integer>rateLimitCache;
    private int maxRequest;
    private int windowSeconds;
    RateLimiter(int maxRequest,int windowSeconds){
        this.rateLimitCache=new TTLCache<>(10000,windowSeconds);
        this.maxRequest=maxRequest;
        this.windowSeconds=windowSeconds;
    }

    boolean isAllowed(String ipAddress){
        Integer count=rateLimitCache.get(ipAddress);
        if(count==null){
            rateLimitCache.put(ipAddress,1);
            return true;
        }
        if(count>=maxRequest){
            return false;
        }
        else{
            rateLimitCache.put(ipAddress,count+1);
            return true;
        }

    }
}
