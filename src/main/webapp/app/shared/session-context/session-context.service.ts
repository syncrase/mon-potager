import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SessionContextService {
  private contextMap = new Map();

  set(key: string, value: any): void {
    this.contextMap.set(key, value);
  }

  get(key: string, dropValue = true): any | undefined {
    const value = this.contextMap.get(key);
    if (dropValue) {
      this.contextMap.delete(key);
    }
    return value;
  }

  clear(): void {
    this.contextMap.clear();
  }
}
