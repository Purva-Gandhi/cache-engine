package org.example.cacheengine;
import org.example.CacheService;
import org.example.LRUCache;
import org.springframework.web.bind.annotation.*;
import org.example.LRUCache;
import org.example.LFUCache;
import org.example.TTLCache;
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService<String,String> cacheService=new CacheService<>(new LRUCache<>(100));

    @GetMapping("/{key}")
    public String get(@PathVariable String key){
        String value= cacheService.get(key);
        return value!=null?value:"null";
    }

    @PutMapping("/{key}")
    public String put(@PathVariable String key,@RequestBody String value){
        cacheService.put(key,value);
        return "stored";
    }

    @DeleteMapping("/{key}")
    public String delete(@PathVariable String key){
        cacheService.delete(key);
        return "deleted";
    }

    @GetMapping("/stats/metrics")
    public String metrics(){
        return cacheService.getMetrics();
    }

    @PostMapping("/config")
    public String switchMapping(@RequestParam String policy,@RequestParam int capacity){
        switch(policy.toUpperCase()){
            case "LRU":
                cacheService.switchPolicy(new LRUCache<>(capacity));
                break;
            case "LFU":
                cacheService.switchPolicy(new LFUCache<>(capacity));
                break;
            case "TTL":
                cacheService.switchPolicy(new TTLCache<>(capacity,300));
                break;
            default:
                return "unknown policy";
        }
        return "switched to"+ policy;
    }

}
