#!/bin/bash

echo "启动MCP服务..."
echo ""
echo "注意：请确保已设置以下环境变量："
echo "  OPENAI_API_KEY=your-api-key"
echo "  OPENAI_BASE_URL=https://api.openai.com (可选，默认为OpenAI官方API)"
echo ""

java -jar target/report-mcp-1.0-SNAPSHOT-jar-with-dependencies.jar
