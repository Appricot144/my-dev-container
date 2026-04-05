package com.example.echo.config;

import com.example.echo.db.DbHealthChecker;
import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * アプリケーション全体の設定クラス。
 *
 * <p>application.properties の {@code db.enabled} を切り替えることで
 * DB接続の有無を制御します。
 */
@Configuration
@ComponentScan(basePackages = "com.example.echo")
@PropertySource("classpath:application.properties")
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    // ---------- DB設定値（application.properties から注入） ----------

    @Value("${db.enabled}")
    private boolean dbEnabled;

    @Value("${db.url:}")
    private String dbUrl;

    @Value("${db.username:}")
    private String dbUsername;

    @Value("${db.password:}")
    private String dbPassword;

    // ----------------------------------------------------------------

    /**
     * DB接続が有効な場合のみ {@link DataSource} を Bean 登録します。
     *
     * <p>{@code db.enabled=false} の場合は null を返すため、
     * 依存する Bean は {@code @Autowired(required=false)} で受け取ってください。
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        if (!dbEnabled) {
            log.info("DB接続モード: 無効 (db.enabled=false)");
            return null;
        }

        log.info("DB接続モード: 有効 (db.enabled=true) → {}", dbUrl);

        OracleDataSource ds = new OracleDataSource();
        ds.setURL(dbUrl);
        ds.setUser(dbUsername);
        ds.setPassword(dbPassword);
        return ds;
    }

    /**
     * DB接続が有効な場合のみ {@link JdbcTemplate} を Bean 登録します。
     */
    @Bean
    public JdbcTemplate jdbcTemplate() throws SQLException {
        DataSource ds = dataSource();
        if (ds == null) {
            return null;
        }
        return new JdbcTemplate(ds);
    }

    /**
     * DB疎通確認用ユーティリティ Bean。
     */
    @Bean
    public DbHealthChecker dbHealthChecker() throws SQLException {
        return new DbHealthChecker(jdbcTemplate(), dbEnabled);
    }
}
