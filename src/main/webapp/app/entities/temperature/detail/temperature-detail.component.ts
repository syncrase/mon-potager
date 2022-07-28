import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITemperature } from '../temperature.model';

@Component({
  selector: 'jhi-temperature-detail',
  templateUrl: './temperature-detail.component.html',
})
export class TemperatureDetailComponent implements OnInit {
  temperature: ITemperature | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ temperature }) => {
      this.temperature = temperature;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
