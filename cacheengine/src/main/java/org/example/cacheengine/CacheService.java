package org.example;
import org.example.EvictionPolicy;
import org.example.MetricsCollector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheService<K, V> {
    private EvictionPolicy<K, V> activePolicy;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final MetricsCollector metrics = new MetricsCollector();
    public CacheService( org.example.EvictionPolicy<K,V> activePolicy) {
        this.activePolicy = activePolicy;
    }
    public V get(K key){
        readLock.lock();
        try {
            V value = activePolicy.get(key);
            if (value != null) {
                metrics.recordHit();
            } else {
                metrics.recordMiss();
            }
            return value;
        }
        finally {
            readLock.unlock();
        }
    }
    public void put(K key,V value){
        writeLock.lock();
        try{
            activePolicy.put(key,value);
        }
        finally {
            writeLock.unlock();
        }
    }
    public void switchPolicy(EvictionPolicy<K,V>newPolicy){
        writeLock.lock();
        try{
            this.activePolicy=newPolicy;
        }
        finally {
            writeLock.unlock();
        }
    }
    public String getMetrics() {
        return metrics.getStats();
    }
    public void delete(K key){
        writeLock.lock();
        try{
            activePolicy.delete(key);
        }
        finally {
            writeLock.unlock();
        }
    }
}
