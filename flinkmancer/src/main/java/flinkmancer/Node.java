/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flinkmancer;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Hergal
 */
public class Node {

    private Long id;
    public ArrayList<Set<Long>> sets;

    //Constructors
    public Node() {
        this.id = null;
        this.sets = new ArrayList<>();
    }

    public Node(Long id) {
        this.id = id;
        this.sets = new ArrayList<>();
    }

    public Node(Long id, ArrayList<Set<Long>> sets) {
        this.id = id;
        this.sets = sets;
    }

    //Setters & Getters
    public ArrayList<Set<Long>> getSets() {
        return this.sets;
    }

    public void setSets(ArrayList<Set<Long>> sets) {
        this.sets = sets;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    //Specific Sets
    //Sets go from 0-7. even inc. odd out. follow - reply - retweet - quote

    public void addSet(Set<Long> s) {
        this.sets.add(s);
    }

    public Set<Long> getSet(int i) {
        return this.sets.get(i);
    }

    public ArrayList<Long> getArrayList(int i) {
        ArrayList<Long> al = new ArrayList<>();
        al.addAll(sets.get(i));
        return al;
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
