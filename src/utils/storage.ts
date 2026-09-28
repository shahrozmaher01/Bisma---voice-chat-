// Safe LocalStorage wrapper with in-memory fallback for sandboxed/cross-origin iframes

class SafeStorage {
  private memoryFallback: Map<string, string> = new Map();
  private isStorageAvailable: boolean | null = null;

  private checkAvailability(): boolean {
    if (this.isStorageAvailable !== null) {
      return this.isStorageAvailable;
    }
    try {
      if (typeof window === 'undefined' || !window.localStorage) {
        this.isStorageAvailable = false;
        return false;
      }
      const testKey = '__aura_storage_test__';
      window.localStorage.setItem(testKey, testKey);
      window.localStorage.removeItem(testKey);
      this.isStorageAvailable = true;
      return true;
    } catch {
      this.isStorageAvailable = false;
      return false;
    }
  }

  getItem(key: string): string | null {
    try {
      if (this.checkAvailability()) {
        const value = window.localStorage.getItem(key);
        if (value !== null) return value;
      }
    } catch (e) {
      console.warn(`[SafeStorage] Failed to read "${key}" from localStorage:`, e);
    }
    return this.memoryFallback.get(key) ?? null;
  }

  setItem(key: string, value: string): void {
    try {
      if (this.checkAvailability()) {
        window.localStorage.setItem(key, value);
      }
    } catch (e) {
      console.warn(`[SafeStorage] Failed to write "${key}" to localStorage:`, e);
    }
    this.memoryFallback.set(key, value);
  }

  removeItem(key: string): void {
    try {
      if (this.checkAvailability()) {
        window.localStorage.removeItem(key);
      }
    } catch (e) {
      console.warn(`[SafeStorage] Failed to remove "${key}" from localStorage:`, e);
    }
    this.memoryFallback.delete(key);
  }

  clear(): void {
    try {
      if (this.checkAvailability()) {
        window.localStorage.clear();
      }
    } catch (e) {
      console.warn('[SafeStorage] Failed to clear localStorage:', e);
    }
    this.memoryFallback.clear();
  }
}

export const safeStorage = new SafeStorage();

export function safeJsonParse<T>(raw: string | null, fallback: T): T {
  if (!raw) return fallback;
  try {
    const parsed = JSON.parse(raw);
    return parsed !== null && parsed !== undefined ? (parsed as T) : fallback;
  } catch (err) {
    console.warn('[SafeStorage] JSON parse error, returning fallback:', err);
    return fallback;
  }
}
