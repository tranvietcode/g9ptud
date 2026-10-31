package com.example.studentmanager.dal;

import java.util.HashMap;

public class Row extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;
    public String s(String key) { String v = get(key); return v == null ? "" : v; }
    public long id(String key) { return Long.parseLong(s(key)); }
}
