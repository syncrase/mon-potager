import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReproduction } from '../reproduction.model';

@Component({
  selector: 'jhi-reproduction-detail',
  templateUrl: './reproduction-detail.component.html',
})
export class ReproductionDetailComponent implements OnInit {
  reproduction: IReproduction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reproduction }) => {
      this.reproduction = reproduction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
