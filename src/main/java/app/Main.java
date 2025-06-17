package app;

import app.model.Student;
import static spark.Spark.*;
import com.google.gson.Gson;
import app.store.*;

public class Main {
    public static void main(String[] args) {
        port(8080);
        ipAddress("0.0.0.0");

        System.out.println("Sunucu başlatılıyor...");

        Gson gson = new Gson();

        RedisStore.init();
        HazelcastStore.init();
        MongoStore.init();

        before((req, res) -> {
            if (req.pathInfo().startsWith("/nosql-lab")) {
                res.type("application/json");
            }
        });

        get("/nosql-lab-rd/ogrenci_no/:id", (req, res) -> {
            Student s = RedisStore.get(req.params("id"));
            if (s == null) {
                res.status(404);
                return "{\"error\":\"Kayıt bulunamadı\"}";
            }
            return gson.toJson(s);
        });

        get("/nosql-lab-hz/ogrenci_no/:id", (req, res) -> {
            Student s = HazelcastStore.get(req.params("id"));
            if (s == null) {
                res.status(404);
                return "{\"error\":\"Kayıt bulunamadı\"}";
            }
            return gson.toJson(s);
        });

        get("/nosql-lab-mon/ogrenci_no/:id", (req, res) -> {
            Student s = MongoStore.get(req.params("id"));
            if (s == null) {
                res.status(404);
                return "{\"error\":\"Kayıt bulunamadı\"}";
            }
            return gson.toJson(s);
        });

        // Ana sayfa için UTF-8 charset eklendi
        get("/", (req, res) -> {
            res.type("text/html; charset=UTF-8"); // <-- Düzeltme burada
            return "<html><body><h2>Hoş geldiniz! NoSQL Lab Sunucusu çalışıyor.</h2></body></html>";
        });

        System.out.println("Sunucu 8080 portunda çalışıyor...");
        awaitInitialization();
    }
}
