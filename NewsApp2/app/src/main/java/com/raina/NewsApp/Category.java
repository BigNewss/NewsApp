package com.raina.NewsApp;

/**
 * Created by pilgrims on 09/09/2017.
 */


public class Category{
    private volatile static Category category;
    public static boolean exist[] = new boolean[12];
    private Category(){
        for(int i = 0; i < 12; i++){
            exist[i] = true;
        }
    }
    private Category(String s){
        String[] ss = s.split(" ");
        for(int i = 0; i < 12; i++){
            exist[i] = ss[i].equals("1");
        }
    }
    public static Category getCategory(String s) {
        if (category == null) {
            synchronized (Category.class) {
                if (category == null) {
                    if(s.equals(""))
                    category= new Category();
                    else{
                        category = new Category(s);
                    }
                }
            }
        }
        return category;
    }
    public static Category getCategory() {
        if (category == null) {
            synchronized (Category.class) {
                if (category == null) {
                    category= new Category();
                }
            }
        }
        return category;
    }
    public static boolean change(int i){
        try {
            exist[i] = !exist[i];
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public static boolean get(int i){
        return exist[i];
    }
}


