package com.raina.NewsApp;

import java.util.*;



/**
 * Created by pilgrims on 13/09/2017.
 */

public class Block {
    private volatile static Block block;
    public static ArrayList<String> blockList= new ArrayList<>();
    private Block(){
    }

    private Block(String s){
        String[] ss = s.split(" ");
        for(int i = 0; i < ss.length; i++){
            blockList.add(ss[i]);
        }
    }
    public static Block getBlock() {
        if (block== null) {
            synchronized (Category.class) {
                if (block== null) {
                    block= new Block();
                }
            }
        }
        return block;
    }
    public static void addBlock(String s)
    {
        blockList.add(s);
    }

    public static void removeBlock(String s){
        blockList.remove(s);
    }
}