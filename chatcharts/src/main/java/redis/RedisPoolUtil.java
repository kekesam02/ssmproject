package redis;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geely
 */
public class RedisPoolUtil {

    public final static String VIRTUAL_COURSE_PREX = "_lc_vc_";

    /**
     * 得到Key
     * @param key
     * @return
     */
    public String buildKey(String key){
        return VIRTUAL_COURSE_PREX + key;
    }

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    //exTime的单位是秒
    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 设置 list
     * @param <T>
     * @param key
     * @param value
     */
    public <T> void setList(String key ,List<T> list){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            jedis.set(key.getBytes(),ObjectTranscoder.serialize(list));
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取list
     * @param <T>
     * @param key
     * @return list
     */
    public <T> List<T> getList(String key){
        Jedis jedis = RedisPool.getJedis();
        String bKey = buildKey(key);
        if(jedis == null || !jedis.exists(key.getBytes())){
            return null;
        }
        byte[] in = jedis.get(key.getBytes());
        List<T> list = (List<T>) ObjectTranscoder.deserialize(in);
        jedis.close();
        return list;
    }
    /**
     * 设置 map
     * @param <T>
     * @param key
     * @param value
     */
    public <T> void setMap(String key ,Map<String,T> map){
        try {
            Jedis jedis = RedisPool.getJedis();
            jedis.set(key.getBytes(),ObjectTranscoder.serialize(map));
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取list
     * @param <T>
     * @param key
     * @return list
     */
    public <T> Map<String,T> getMap(String key){
        String bKey = buildKey(key);
        Jedis jedis = RedisPool.getJedis();
        if(jedis == null || !jedis.exists(key.getBytes())){
            return null;
        }
        byte[] in = jedis.get(key.getBytes());
        Map<String,T> map = (Map<String, T>) ObjectTranscoder.deserialize(in);
        jedis.close();
        return map;
    }

    public static void main(String[] args) {

        List<HashMap<String,Object>> positions = new ArrayList<>();
        HashMap<String,Object> position = new HashMap<>();
        position.put("name","鄂尔多斯");
        position.put("value",12);
        position.put("lan","125.03");
        position.put("glan","46.58");

        positions.add(position);

        RedisPoolUtil redisPoolUtil =  new RedisPoolUtil();
        redisPoolUtil.setList("positions",positions);


        List<HashMap<String,Object>> positions2 = redisPoolUtil.getList("positions");
        System.out.println(positions2);

    }


}