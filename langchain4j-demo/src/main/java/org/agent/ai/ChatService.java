package org.agent.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;
public interface ChatService {

    @SystemMessage("""
            You are good at writing technical documents with the tools below:
            getDailyCommits tool – use only when the user explicitly asks for a daily report.
            For any other request, ignore both tools and answer as a normal AI.
            """)
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);
}
