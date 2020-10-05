export default class SWRVCache {
    constructor(ttl = 0) {
        this.items = new Map();
        this.ttl = ttl;
    }
    /**
     * Get cache item while evicting
     */
    get(k) {
        return this.items.get(k);
    }
    set(k, v, ttl) {
        const timeToLive = ttl || this.ttl;
        const now = Date.now();
        const item = {
            data: v,
            createdAt: now,
            expiresAt: timeToLive ? now + timeToLive : Infinity
        };
        timeToLive && setTimeout(() => {
            const current = Date.now();
            const hasExpired = current >= item.expiresAt;
            if (hasExpired)
                this.delete(k);
        }, timeToLive);
        this.items.set(k, item);
    }
    delete(k) {
        this.items.delete(k);
    }
}
