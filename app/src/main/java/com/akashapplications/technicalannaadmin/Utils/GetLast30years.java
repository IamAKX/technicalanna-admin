package com.akashapplications.technicalannaadmin.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class GetLast30years {
    public static ArrayList<Integer> getYears()
    {
        ArrayList<Integer> list = new ArrayList<>();
        int currYear = Calendar.getInstance().get(Calendar.YEAR)-1;
        int limit = currYear - 30;
        while(currYear > limit)
        {
            list.add(currYear);
            currYear--;
        }
        return list;
    }

}
