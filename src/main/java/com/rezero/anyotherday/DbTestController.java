package com.rezero.anyotherday;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
// ㄹㅇ 그저 데이터 연결됐는지 확인용
@RestController
public class DbTestController {

    private final JdbcTemplate jdbcTemplate;

    public DbTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db-test")
    public String ping() {
        return "db-test alive";
    }

    // guardian 전체 조회
    @GetMapping("/db-test/guardians")
    public List<Map<String, Object>> guardians() {
        return jdbcTemplate.queryForList("SELECT * FROM guardian");
    }
}