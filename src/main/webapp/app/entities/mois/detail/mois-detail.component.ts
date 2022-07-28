import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMois } from '../mois.model';

@Component({
  selector: 'jhi-mois-detail',
  templateUrl: './mois-detail.component.html',
})
export class MoisDetailComponent implements OnInit {
  mois: IMois | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mois }) => {
      this.mois = mois;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
