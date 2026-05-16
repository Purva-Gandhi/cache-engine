package org.example;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {
    private AtomicLong hitCount = new AtomicLong(0);
    private AtomicLong missCount = new AtomicLong(0);
    private AtomicLong evictionCount = new AtomicLong(0);

    public void recordHit() {
        hitCount.incrementAndGet();
    }

    public void recordMiss() {
        missCount.incrementAndGet();
    }

    public void recordEviction() {
        evictionCount.incrementAndGet();
    }

    public double getHitRate() {
        long total = hitCount.get() + missCount.get();
        if (total == 0) return 0;
        return (hitCount.get() * 100.0) / total;
    }

    public String getStats() {
        return "Hits: " + hitCount.get() +
                ", Misses: " + missCount.get() +
                ", Evictions: " + evictionCount.get() +
                ", HitRate: " + String.format("%.2f", getHitRate()) + "%";
    }
}
