package com.logic.project.config;

import com.logic.project.service.DemoSeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "demo.seed.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {
    private final DemoSeedService demoSeedService;
    @Value("${demo.seed.exit-after:false}")
    private boolean exitAfter;

    @Override
    public void run(ApplicationArguments args) {
        demoSeedService.seed();
        System.out.println("시연 데이터 확인 완료: 현장 10 / 장비 40 / 유지보수 18 / 관제 이벤트 24 (기존 데이터 보존)");
    }

    // 별도 임시 포트에서 seed만 실행할 때 사용합니다. 기존 8081 서버는 중단하지 않습니다.
    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        if (exitAfter) event.getApplicationContext().close();
    }
}
