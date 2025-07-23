#!/usr/bin/env python3
"""
MCP服务测试脚本
用于测试Git提交记录日报生成MCP服务
"""

import json
import subprocess
import sys
import os

def send_mcp_request(process, request):
    """发送MCP请求"""
    request_json = json.dumps(request)
    print(f"发送请求: {request_json}")
    
    process.stdin.write(request_json + '\n')
    process.stdin.flush()
    
    # 读取响应
    response_line = process.stdout.readline()
    if response_line:
        response = json.loads(response_line.strip())
        print(f"收到响应: {json.dumps(response, indent=2, ensure_ascii=False)}")
        return response
    return None

def test_mcp_service():
    """测试MCP服务"""
    
    # 启动MCP服务
    jar_path = "target/report-mcp-1.0-SNAPSHOT-jar-with-dependencies.jar"
    if not os.path.exists(jar_path):
        print(f"错误: 找不到jar文件 {jar_path}")
        print("请先运行 'mvn package -DskipTests' 构建项目")
        return False
    
    print("启动MCP服务...")
    process = subprocess.Popen(
        ["java", "-jar", jar_path],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1
    )
    
    try:
        # 1. 测试初始化
        print("\n=== 测试初始化 ===")
        init_request = {
            "jsonrpc": "2.0",
            "id": "1",
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "clientInfo": {
                    "name": "test-client",
                    "version": "1.0.0"
                }
            }
        }
        
        init_response = send_mcp_request(process, init_request)
        if not init_response or "result" not in init_response:
            print("初始化失败")
            return False
        
        # 2. 测试工具列表
        print("\n=== 测试工具列表 ===")
        list_tools_request = {
            "jsonrpc": "2.0",
            "id": "2",
            "method": "tools/list",
            "params": {}
        }
        
        list_response = send_mcp_request(process, list_tools_request)
        if not list_response or "result" not in list_response:
            print("获取工具列表失败")
            return False
        
        # 3. 测试工具调用（需要提供实际的Git仓库路径）
        print("\n=== 测试工具调用 ===")
        repo_path = input("请输入Git仓库路径（按回车跳过工具调用测试）: ").strip()
        
        if repo_path:
            call_tool_request = {
                "jsonrpc": "2.0",
                "id": "3",
                "method": "tools/call",
                "params": {
                    "name": "generate_daily_report",
                    "arguments": {
                        "repository_path": repo_path,
                        "date": "2025-01-22"
                    }
                }
            }
            
            call_response = send_mcp_request(process, call_tool_request)
            if call_response:
                print("工具调用成功")
            else:
                print("工具调用失败")
        else:
            print("跳过工具调用测试")
        
        print("\n=== 测试完成 ===")
        return True
        
    except Exception as e:
        print(f"测试过程中发生错误: {e}")
        return False
    
    finally:
        # 关闭进程
        process.terminate()
        process.wait()

if __name__ == "__main__":
    print("Git提交记录日报生成MCP服务测试")
    print("=" * 50)
    
    success = test_mcp_service()
    
    if success:
        print("\n✅ MCP服务测试通过")
    else:
        print("\n❌ MCP服务测试失败")
        sys.exit(1)
