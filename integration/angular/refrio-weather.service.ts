// Optional integration example: copy into src/app/operations/infrastructure/ in Angular Refrio.
// Wire it into an actual view before claiming the third-party service is integrated end-to-end.
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
export interface RefrioAmbientWeather {
  location: { latitude: number; longitude: number };
  temperatureCelsius: number;
  humidityPercent: number;
  observedAt: string;
  provider: string;
}
@Injectable({ providedIn: 'root' })
export class RefrioWeatherService {
  private readonly http = inject(HttpClient);
  current(latitude: number, longitude: number): Observable<RefrioAmbientWeather> {
    const params = new HttpParams()
      .set('latitude', String(latitude))
      .set('longitude', String(longitude));
    return this.http.get<RefrioAmbientWeather>(
      `${environment.serverBasePath}/v1/operations/weather`, { params });
  }
}
