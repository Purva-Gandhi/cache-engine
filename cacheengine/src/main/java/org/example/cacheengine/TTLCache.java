package org.example;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TTLCache<K,V> implements EvictionPolicy<K,V> {
    private HashMap<K,Node<K,V>>hashMap;
    private int capacity;
    private long defaultTtl;
    public TTLCache(int capacity,long defaultTtl){
        this.hashMap=new HashMap<>();
        this.capacity=capacity;
        this.defaultTtl=defaultTtl;
        startCleanup();
    }
    private void startCleanup() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            hashMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }, 30, 30, TimeUnit.SECONDS);
    }
    public V get(K key){
       if(!hashMap.containsKey(key)){
           return null;
       }
       Node<K,V>nodeInCache= hashMap.get(key);
       if(nodeInCache.isExpired()){
           hashMap.remove(key);
           return null;
       }

      return nodeInCache.getValue();

    }
    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }
    public void put(K key,V value,long ttlseconds){

           if(hashMap.containsKey(key)){
               Node<K,V>existingNode=hashMap.get(key);
               existingNode.setValue(value);
               existingNode.setExpiryTime(ttlseconds);
               return;

           }
           if(hashMap.size()==capacity){
               K firstKey = hashMap.keySet().iterator().next();
               hashMap.remove(firstKey);
           }
        Node<K,V> newNode = new Node<>(key, value, ttlseconds);
        hashMap.put(key, newNode);
    }
    public void delete(K key){
        hashMap.remove(key);
    }
}
