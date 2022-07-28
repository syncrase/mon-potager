import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFeuillage } from '../feuillage.model';

@Component({
  selector: 'jhi-feuillage-detail',
  templateUrl: './feuillage-detail.component.html',
})
export class FeuillageDetailComponent implements OnInit {
  feuillage: IFeuillage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feuillage }) => {
      this.feuillage = feuillage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
