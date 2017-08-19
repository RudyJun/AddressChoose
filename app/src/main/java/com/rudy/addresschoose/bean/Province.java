package com.rudy.addresschoose.bean;

import java.util.List;

/**
 * Created by liuzaijun on 2017/8/16.
 */

public class Province {

    public String name;
    public List<City> array;

    public static class City {
        public String name;
        public List<Region> array;
    }

    public static class Region {
        public String name;
        public List<String> array;
    }
}
