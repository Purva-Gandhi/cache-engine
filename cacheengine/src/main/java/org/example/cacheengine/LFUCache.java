package org.example;

import java.util.HashMap;
import java.util.LinkedList;

public class LFUCache<K,V> implements EvictionPolicy<K,V> {
    private int minFreq;
    private int capacity;
    private HashMap<K,Node<K,V>>keyMap;
    private HashMap<Integer, LinkedList<Node<K,V>>>freqMap;

    public LFUCache(int capacity){
        minFreq=0;
        this.capacity=capacity;
        this.keyMap=new HashMap<>();
        this.freqMap=new HashMap<>();
    }
    private void promoteNode(Node<K,V>node){
        int currFreq=node.getFreq();
        LinkedList<Node<K,V>>getList=freqMap.get(currFreq);
        getList.remove(node);
        if(getList.isEmpty() &&currFreq==minFreq){
            minFreq+=1;
        }
        node.setFreq(currFreq+1);
        currFreq+=1;
        LinkedList<Node<K,V>>list=freqMap.get(currFreq);

        if(list==null){
            LinkedList<Node<K,V>>newList=new LinkedList<>();
            newList.add(node);
            freqMap.put(currFreq,newList);
        }
        else{
            list.addFirst(node);
        }

       }

       public V get(K key){
        if(!keyMap.containsKey(key)){
            return null;
        }

            Node<K,V>node=keyMap.get(key);
            promoteNode(node);
            return (node.getValue());


       }
       public void put(K key,V value){

        if(keyMap.containsKey(key)){
            Node<K,V> existingNode=keyMap.get(key);
            existingNode.setValue(value);
            promoteNode(existingNode);
            return;
        }
        Node<K,V>newNode=new Node<K,V>(key,value);
        LinkedList<Node<K,V>>list=freqMap.get(minFreq);
        if(keyMap.size()==capacity){
          Node<K,V>minFreqNode=list.getLast();
          list.removeLast();
          keyMap.remove(minFreqNode.getKey());

        }

        keyMap.put(newNode.getKey(),newNode);

           LinkedList<Node<K,V>> freq1List = freqMap.getOrDefault(1, new LinkedList<>());
           freq1List.addFirst(newNode);
           freqMap.put(1, freq1List);

           minFreq=1;


       }
       public void delete(K key){
          Node<K,V> node=keyMap.get(key);
          keyMap.remove(key);
          freqMap.get(node.getFreq()).remove(node);
       }



}
