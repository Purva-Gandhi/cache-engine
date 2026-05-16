package org.example;

public class Node <K,V>{
   private K key;
    private V value;
    Node<K,V>prev;
    Node<K,V>next;
    private int freq;
    private long expiryTime;

    public Node(K key,V value){
        this.key=key;
        this.value=value;
        this.freq=1;

    }
    public Node(K key,V value,long ttlSeconds){
        this.key=key;
        this.value=value;
        this.freq=1;
        this.expiryTime=System.currentTimeMillis()+(ttlSeconds*1000);
    }
    public void setValue(V value){
        this.value=value;
    }
    public void setFreq(int freq){
        this.freq=freq;
    }
    public void setExpiryTime(long ttlSeconds) {
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }
    public K getKey() {
        return key;
    }
    public V getValue() {
        return value;
    }
    public int getFreq(){
        return freq;
    }
    public long getExpiryTime(){
        return expiryTime;
    }
    public boolean  isExpired(){
        return expiryTime!=0 && System.currentTimeMillis()>expiryTime;
    }

}
