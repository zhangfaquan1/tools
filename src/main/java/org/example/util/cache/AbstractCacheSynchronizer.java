package org.example.util.cache;

import org.example.util.function.Simple;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public abstract class AbstractCacheSynchronizer {

    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    protected <R> R getData(Supplier<R> getCache, Simple writeCache) {
        // 首先开启读锁，从缓存中读取
        rwl.readLock().lock();
        R data = getCache.get();

        // 若缓存不存在，则读取数据，写入缓存
        if (data == null) {
            // 获取写锁前释放读锁
            rwl.readLock().unlock();
            rwl.writeLock().lock();
            try {
                // 判断是否有其它线程已经获取了写锁、更新了缓存, 避免重复更新
                if (getCache.get() == null) {
                    writeCache.operate();
                }
                // 锁降级, 这样能够让其它线程读取缓存。
                // 这里加读锁是为了避免下面操作数据时受到写锁的影响。
                rwl.readLock().lock();
            } finally {
                rwl.writeLock().unlock();
            }

            try {
                data = getCache.get();
            } finally {
                rwl.readLock().unlock();
            }
        }
        return data;
    }
}
