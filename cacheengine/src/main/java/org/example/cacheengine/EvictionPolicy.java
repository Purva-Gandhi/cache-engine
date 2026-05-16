package org.example;



public interface EvictionPolicy <K,V> {

    public V get(K key);

    public  void put(K key,V value);

    public void delete(K key);
}
