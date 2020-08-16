import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {
    constructor() {
    }

  public getWS(wsUrl: string): WebSocket {
    return new WebSocket(wsUrl);
  }

  public getInt64(dv: DataView, byteOffset, littleEndian): number {
    let low = byteOffset + 4;
    let high = byteOffset;
    if (littleEndian) {
      low = byteOffset;
      high = byteOffset + 4;
    }
    return ((dv.getUint32(high, littleEndian) << 32) | dv.getUint32(low, littleEndian));
  }
}
