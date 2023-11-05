package edu.doudou.NanqiangTakenout.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Res<T> {
    private Integer code;

    private String msg;

    private T data;

    private Map map = new HashMap();

    public static <T> Res<T> success(T object) {
        Res<T> r = new Res<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Res<T> error(String msg) {
        Res r = new Res();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Res<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
