package com.example.echo.controller;

import com.example.echo.db.DbHealthChecker;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * テスト用 Echo サーバのコントローラ。
 *
 * <h2>エンドポイント一覧</h2>
 * <ul>
 *   <li>{@code GET/POST /echo}        - リクエスト内容をそのまま返す</li>
 *   <li>{@code GET     /health}       - サーバ＆DB の死活確認</li>
 *   <li>{@code GET     /info}         - サーバ情報の表示</li>
 * </ul>
 */
@RestController
public class EchoController {

    private static final Logger log = LoggerFactory.getLogger(EchoController.class);

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${db.enabled}")
    private boolean dbEnabled;

    /** db.enabled=false のとき null になるため required=false */
    @Autowired(required = false)
    private DbHealthChecker dbHealthChecker;

    // ----------------------------------------------------------------
    // GET /echo
    // ----------------------------------------------------------------

    /**
     * GET リクエストをエコーバックします。
     *
     * <p>クエリパラメータ、ヘッダー、リクエスト情報を JSON で返します。
     */
    @GetMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> echoGet(HttpServletRequest request) {
        log.info("GET /echo from {}", request.getRemoteAddr());
        return ResponseEntity.ok(buildEchoResponse(request, null));
    }

    // ----------------------------------------------------------------
    // POST /echo
    // ----------------------------------------------------------------

    /**
     * POST リクエストをエコーバックします。
     *
     * <p>リクエストボディを文字列として受け取り、そのまま返します。
     */
    @PostMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> echoPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        log.info("POST /echo from {} body.length={}", request.getRemoteAddr(),
                body == null ? 0 : body.length());
        return ResponseEntity.ok(buildEchoResponse(request, body));
    }

    // ----------------------------------------------------------------
    // GET /health
    // ----------------------------------------------------------------

    /**
     * サーバとDB の死活確認エンドポイント。
     *
     * <p>DB接続エラー時は HTTP 503 を返します。
     */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("server", "UP");
        res.put("timestamp", Instant.now().toString());

        if (dbHealthChecker != null) {
            DbHealthChecker.DbStatus dbStatus = dbHealthChecker.check();
            res.put("db", Map.of(
                    "status", dbStatus.status(),
                    "message", dbStatus.message()
            ));

            if ("ERROR".equals(dbStatus.status())) {
                return ResponseEntity.status(503).body(res);
            }
        } else {
            res.put("db", Map.of(
                    "status", "DISABLED",
                    "message", "DB接続モードは無効です (db.enabled=false)"
            ));
        }

        return ResponseEntity.ok(res);
    }

    // ----------------------------------------------------------------
    // GET /info
    // ----------------------------------------------------------------

    /**
     * サーバ情報を返すエンドポイント。
     */
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("app", appName);
        res.put("version", appVersion);
        res.put("dbEnabled", dbEnabled);
        res.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(res);
    }

    // ----------------------------------------------------------------
    // 内部ユーティリティ
    // ----------------------------------------------------------------

    /**
     * リクエスト情報を Map に変換します。
     */
    private Map<String, Object> buildEchoResponse(HttpServletRequest request, String body) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("timestamp", Instant.now().toString());
        res.put("method", request.getMethod());
        res.put("uri", request.getRequestURI());
        res.put("remoteAddr", request.getRemoteAddr());

        // クエリパラメータ
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            Map<String, Object> params = new LinkedHashMap<>();
            paramMap.forEach((k, v) -> params.put(k, v.length == 1 ? v[0] : Arrays.asList(v)));
            res.put("queryParams", params);
        }

        // リクエストヘッダ
        Map<String, String> headers = new LinkedHashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.put(name, request.getHeader(name)));
        res.put("headers", headers);

        // リクエストボディ（POST 用）
        if (body != null) {
            res.put("body", body);
        }

        return res;
    }
}
