package com.example.echo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB への疎通確認を行うユーティリティクラス。
 *
 * <p>{@code db.enabled=false} の場合は常に「無効」を返します。
 */
public class DbHealthChecker {

    private static final Logger log = LoggerFactory.getLogger(DbHealthChecker.class);

    /** Oracle DB の疎通確認クエリ */
    private static final String PING_QUERY = "SELECT 1 FROM DUAL";

    private final JdbcTemplate jdbcTemplate;
    private final boolean dbEnabled;

    public DbHealthChecker(JdbcTemplate jdbcTemplate, boolean dbEnabled) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbEnabled = dbEnabled;
    }

    /**
     * DB接続を確認し、結果を返します。
     *
     * @return 確認結果を表す {@link DbStatus}
     */
    public DbStatus check() {
        if (!dbEnabled) {
            return DbStatus.disabled();
        }
        try {
            jdbcTemplate.queryForObject(PING_QUERY, Integer.class);
            return DbStatus.ok();
        } catch (Exception e) {
            log.error("DB疎通確認失敗: {}", e.getMessage());
            return DbStatus.error(e.getMessage());
        }
    }

    // ----------------------------------------------------------------

    /**
     * DB接続確認の結果を表す値オブジェクト。
     */
    public record DbStatus(String status, String message) {

        static DbStatus ok() {
            return new DbStatus("OK", "DB接続に成功しました");
        }

        static DbStatus disabled() {
            return new DbStatus("DISABLED", "DB接続モードは無効です (db.enabled=false)");
        }

        static DbStatus error(String detail) {
            return new DbStatus("ERROR", "DB接続に失敗しました: " + detail);
        }
    }
}
