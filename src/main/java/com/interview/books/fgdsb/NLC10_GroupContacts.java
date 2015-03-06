package com.interview.books.fgdsb;

import com.interview.books.question300.TQ2_UnionFind;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午10:21
 */
public class NLC10_GroupContacts {
    class Contact{
        String name;
        List<String> emails;
        public Contact(String name, String... emails){
            this.name = name;
            this.emails = Arrays.asList(emails);
        }
    }

    public List<List<Contact>> group(List<Contact> contacts){
        Map<String, List<Integer>> emailMap = new HashMap();
        for(int i = 0; i < contacts.size(); i++){
            for(String email : contacts.get(i).emails){
                if(emailMap.containsKey(email)){
                    emailMap.get(email).add(i);
                } else {
                    List<Integer> idx = new ArrayList();
                    idx.add(i);
                    emailMap.put(email, idx);
                }
            }
        }

        TQ2_UnionFind uf = new TQ2_UnionFind(contacts.size());
        for(List<Integer> group : emailMap.values()){
            for(int i = 0; i < group.size() - 1; i++){
                uf.connected(group.get(i), group.get(i + 1));
            }
        }

        Map<Integer, List<Integer>> groupIdx = new HashMap();
        for(int i = 0; i < contacts.size(); i++){
            int p = uf.find(i);
            if(groupIdx.containsKey(p)) groupIdx.get(p).add(i);
            else {
                List<Integer> idx = new ArrayList();
                idx.add(i);
                groupIdx.put(p, idx);
            }
        }

        List<List<Contact>> groups = new ArrayList();
        for(List<Integer> group : groupIdx.values()){
            List<Contact> contactGroup = new ArrayList();
            for(Integer idx : group) contactGroup.add(contacts.get(idx));
            groups.add(contactGroup);
        }
        return groups;
    }
}
