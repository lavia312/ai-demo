package org.agent.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * MCP数据库工具服务
 * 提供数据库操作的核心功能，支持动态SQL生成和执行
 * @author djw
 * @date 2025-01-17
 */
@Service
public class DBToolService {
    private static final Logger logger = LoggerFactory.getLogger(DBToolService.class);
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    @Tool(name = "queryDB", description = "用于查询数据库中有哪些表")
    public String viewDB(){
        logger.info("====查询db情况=========");
        List<Map<String, Object>> tables = jdbcTemplate.queryForList("show tables");
        return JSONObject.toJSONString(tables);
    }

    @Tool(name = "queryTable", description = "用于查询数据库中表的结构")
    public String viewTable(@ToolParam(description = "tableName") String tableName){
        logger.info("====查询db中表的情况=========");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("show columns from " + tableName);
        return JSONObject.toJSONString(maps);
    }

    @Tool(name="querySql",description = "用于查询数据库中的表的数据")
    public String viewTableData(@ToolParam(description = "sql") String sql){
        logger.info("====查询sql=========");
        if(!sql.startsWith("select")){
            throw new RuntimeException("sql must start with select");
        }
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return JSONObject.toJSONString(maps);
    }
}
