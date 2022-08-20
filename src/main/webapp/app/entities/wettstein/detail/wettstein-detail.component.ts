import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWettstein } from '../wettstein.model';

@Component({
  selector: 'jhi-wettstein-detail',
  templateUrl: './wettstein-detail.component.html',
})
export class WettsteinDetailComponent implements OnInit {
  wettstein: IWettstein | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wettstein }) => {
      this.wettstein = wettstein;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
