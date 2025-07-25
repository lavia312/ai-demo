package org.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Git提交记录服务
 * 用于获取和解析Git提交信息
 * @author djw
 * @date 2025-01-22
 */
@Service
public class GitCommitService {
    
    private static final Logger logger = LoggerFactory.getLogger(GitCommitService.class);
    
    /**
     * 提交信息实体类
     */
    public record CommitInfo(String taskId,String message){}
    
    /**
     * 获取指定日期的Git提交记录
     */
    public List<CommitInfo> getCommitsByDate(String repositoryPath, String date) {
        List<CommitInfo> commits = new ArrayList<>();
        
        try {
            // 验证Git仓库
            if (!isGitRepository(repositoryPath)) {
                logger.warn("指定路径不是Git仓库：{}", repositoryPath);
                return commits;
            }
            
            // 构建Git命令
            String[] command = {
                "git", "log",
                "--since="+date ,
                "--pretty=format:%an %s"  //作者 提交信息
            };
            
            logger.info("执行Git命令：{}", String.join(" ", command));
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(repositoryPath));
            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                commits = parseGitLog(reader);
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Git命令执行失败，退出码：{}", exitCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        logger.error("Git错误输出：{}", errorLine);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("获取Git提交记录失败", e);
        }
        
        logger.info("获取到{}条提交记录", commits.size());
        return commits;
    }
    
    /**
     * 检查是否为Git仓库
     */
    private boolean isGitRepository(String path) {
        File gitDir = new File(path, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }
    
    /**
     * 解析Git日志输出
     */
    private List<CommitInfo> parseGitLog(BufferedReader reader) throws IOException {
        List<CommitInfo> commits = new ArrayList<>();
        String line;
        CommitInfo currentCommit;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }
            //dongjiawei task #II4B 调整日志,事务
            String[] commitInfo = line.split("\\s+");
            String author = commitInfo[0];
            String taskId = commitInfo[2];
            // 过滤调合并等一些非业务记录
            if(!taskId.startsWith("#") || !author.equals("dongjiawei")){
                continue;
            }
            String message = commitInfo[3];
            if (commitInfo.length>4){
                for(int i=4;i<commitInfo.length;i++){
                    message+=" "+commitInfo[i];
                }
            }
            currentCommit = new CommitInfo(taskId, message);
            commits.add(currentCommit);
        }
        return commits;
    }

}
