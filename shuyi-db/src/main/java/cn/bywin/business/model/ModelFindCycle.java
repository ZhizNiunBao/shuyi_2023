package cn.bywin.business.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelFindCycle {
    List<String> nameList = new ArrayList<>();
    HashMap<String,List<String>> dataMap = new HashMap<>();
    public void addRote(String start,String end){
        if( nameList.indexOf( start )< 0 ){
            nameList.add(start);
        }
        if( nameList.indexOf( end )< 0 ){
            nameList.add(end );
        }
        List<String> data = dataMap.get( start );
        if( data  == null )
            data  = new ArrayList<>();
        data.add(end);
        dataMap.put(start,data);

        if( !dataMap.containsKey( end ) ){
            dataMap.put(end,null);
        }
    }
    public List<List<String>> find(){
        List<List<String>> retList = new ArrayList<>();
        for( String s:nameList){
            List<String> resultList = new ArrayList<>();
            resultList.add(s);
            retList.add( resultList );
            findRote( s, resultList,retList);
        }
        return retList;
    }
    public void findRote( String node,List<String> resultList,List<List<String>> retList){

        List<String> data = dataMap.get(node);
        if( data != null ){
            List<String> temp = new ArrayList<>();
            temp.addAll( resultList);
            for(int i =0;i< data.size();i++){
                String s = data.get(i);
                if( i == 0 ){
                    if( resultList.indexOf(s)>=0 ) {
                        //cycle
                        resultList.add(s);
                        resultList.add("cycle");
                    }
                    else{
                        resultList.add(s);
                        findRote(s, resultList, retList);
                    }
                }
                else{
                    List<String> resultList2 = new ArrayList<>();
                    resultList2.addAll(temp);
                    if( resultList2.indexOf(s)>=0 ) {
                        //cycle
                        resultList2.add(s);
                        resultList2.add("cycle");
                    }
                    else {
                        resultList2.add(s);
                        retList.add(resultList2);
                        findRote(s, resultList2, retList);
                    }
                }
            }
        }
    }

    /*public static void main(String[] args) {
        ModelFindCycle fc = new ModelFindCycle();
        fc.addRote("A","B");
        fc.addRote("A","E");
        fc.addRote("B","C");
        fc.addRote("C","D");
        fc.addRote("C","E");
        fc.addRote("E","A");

        List<String> start = new ArrayList<>();
        start.add("A");
        start.add("G");
        List<List<String>> lists = fc.find();
        for ( List<String> list1: lists){
            for(String s:list1){
                System.out.print(s+" ");
            }
            System.out.println("\r\n");
        }
    }*/
}
