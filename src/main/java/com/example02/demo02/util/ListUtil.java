package com.example02.demo02.util;

import com.example02.demo02.bean.entity.User;

import java.util.List;

public class ListUtil {

    /**
     * 将用户插入到列表中的顺序位置
     * @param list
     * @param user
     */
    public static void addToList(List<User> list, User user){

        for (int i = 0;i<list.size();i++){
            if (list.get(i).getMark()<user.getMark()){
                list.add(i, user);
                return;
            }
        }
        list.add(user);
    }

    /**
     * 将用户从列表中删除
     * @param list
     * @param user
     */
    public static boolean deleteFromList(List<User> list, User user){

        boolean res = false;

        for (int i = 0;i<list.size();i++){
            if (list.get(i).getUser_id() == user.getUser_id()){
                list.remove(i);
                res = true;
            }
        }

        return res;
    }

    public static User deleteFromList(List<User> list, String id){

        User res = null;

        for (int i = 0;i<list.size();i++){
            if ((list.get(i).getUser_id()+"").equals(id)){
                res = list.remove(i);
            }
        }

        return res;
    }

}
