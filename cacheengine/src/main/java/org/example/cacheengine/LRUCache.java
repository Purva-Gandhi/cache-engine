package org.example;
import java.util.HashMap;

import java.util.*;

public class LRUCache<K,V> implements EvictionPolicy<K,V>{
    private HashMap<K,Node<K,V>>hashMap;
    private int capacity;
    private Node<K,V> head;
    private Node<K,V> tail;
    public LRUCache(int capacity ){
         this.hashMap=new HashMap<>();
        this.capacity=capacity;
        this.head=new Node<>(null,null);
        this.tail=new Node<>(null,null);
        head.next=tail;
        tail.prev=head;


    }

    private void addToFront(Node<K,V>newNode){
        Node<K,V>temp=head.next;
        head.next=newNode;
        newNode.next=temp;
        temp.prev=newNode;
        newNode.prev=head;

    }

    private void removeNode(Node<K,V>node){
         Node<K,V>previousNode=node.prev;
         Node<K,V>nextNode=node.next;
         previousNode.next=nextNode;
         nextNode.prev=previousNode;

    }
    public V get(K key){
        if(!hashMap.containsKey(key)){
            return null;
        }

            Node<K,V>cacheNode=hashMap.get(key);
            removeNode(cacheNode);
            addToFront(cacheNode);

        return cacheNode.getValue();
    }
    public void  put(K key,V value){
        if(hashMap.containsKey(key)){
            Node<K,V> cacheNode=hashMap.get(key);
            removeNode(cacheNode);
            cacheNode.setValue(value);
            addToFront(cacheNode);
            return ;
        }

        Node<K,V> newNode= new Node(key,value);
        if(hashMap.size()==capacity){
            Node<K,V> lastNode=tail.prev;
            removeNode(lastNode);
            hashMap.remove(lastNode.getKey());


        }
        hashMap.put(key,newNode);
        addToFront(newNode);
    }
    public void delete(K key){

        removeNode(hashMap.get(key));
        hashMap.remove(key);
    }
}
