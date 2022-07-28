import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPeriodeAnnee } from '../periode-annee.model';

@Component({
  selector: 'jhi-periode-annee-detail',
  templateUrl: './periode-annee-detail.component.html',
})
export class PeriodeAnneeDetailComponent implements OnInit {
  periodeAnnee: IPeriodeAnnee | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ periodeAnnee }) => {
      this.periodeAnnee = periodeAnnee;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
