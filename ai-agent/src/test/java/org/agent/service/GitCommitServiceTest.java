package org.agent.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

/**
 * Git提交服务测试
 * @author djw
 * @date 2025-01-22
 */
@SpringBootTest
public class GitCommitServiceTest {

    @Test
    public void testGetCommitsByDate() {
        GitCommitService gitCommitService = new GitCommitService();
        
        // 测试获取当前项目的提交记录
        String repoPath = System.getProperty("user.dir");
        LocalDate today = LocalDate.now();
        
        List<GitCommitService.CommitInfo> commits = gitCommitService.getCommitsByDate(repoPath, today);
        
        System.out.println("找到 " + commits.size() + " 条提交记录");
        for (GitCommitService.CommitInfo commit : commits) {
            System.out.println("提交: " + commit.getHash());
            System.out.println("作者: " + commit.getAuthor());
            System.out.println("时间: " + commit.getCommitTimeFormatted());
            System.out.println("消息: " + commit.getMessage());
            System.out.println("文件: " + commit.getChangedFiles());
            System.out.println("---");
        }
    }
    
    @Test
    public void testGetCommitStatistics() {
        GitCommitService gitCommitService = new GitCommitService();
        
        String repoPath = System.getProperty("user.dir");
        LocalDate today = LocalDate.now();
        
        List<GitCommitService.CommitInfo> commits = gitCommitService.getCommitsByDate(repoPath, today);
        String statistics = gitCommitService.getCommitStatistics(commits);
        
        System.out.println("统计信息: " + statistics);
    }
}
