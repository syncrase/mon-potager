import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlante } from '../plante.model';

@Component({
  selector: 'jhi-plante-detail',
  templateUrl: './plante-detail.component.html',
})
export class PlanteDetailComponent implements OnInit {
  plante: IPlante | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plante }) => {
      this.plante = plante;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
