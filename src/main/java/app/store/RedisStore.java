package app.store;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import app.model.Student;
import com.google.gson.Gson;

public class RedisStore {
    static Jedis jedis;
    static Gson gson = new Gson();

    public static void init() {
        try {
            jedis = new Jedis("127.0.0.1", 6379);
            // Bağlantıyı test et
            String pong = jedis.ping();
            if (!"PONG".equals(pong)) {
                System.err.println("Redis bağlantısı başarısız: ping yanıtı " + pong);
                return;
            }

            // Veri ekleme
            for (int i = 0; i < 10000; i++) {
                String id = "2025" + String.format("%06d", i);
                Student s = new Student(id, "Ad Soyad " + i, "Bilgisayar");
                jedis.set(id, gson.toJson(s));
            }
            System.out.println("Redis verileri başarıyla yüklendi.");
        } catch (JedisConnectionException e) {
            System.err.println("Redis bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Student get(String id) {
        try {
            String json = jedis.get(id);
            if (json == null) {
                return null;
            }
            return gson.fromJson(json, Student.class);
        } catch (JedisConnectionException e) {
            System.err.println("Redis get hatası: " + e.getMessage());
            return null;
        }
    }
}
