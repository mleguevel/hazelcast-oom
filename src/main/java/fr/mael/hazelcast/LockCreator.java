package fr.mael.hazelcast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

@Component
public class LockCreator {

    private final FencedLock fencedLock;

    public LockCreator(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        this.fencedLock = hazelcastInstance.getCPSubsystem().getLock("myLock");
    }

    @PostConstruct
    public void getLock() {
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {
            fencedLock.tryLock();
            while(true) {
                if (fencedLock.tryLock()) {
                    fencedLock.unlock();
                }
            }
        });
    }
}
