import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGermination } from '../germination.model';

@Component({
  selector: 'jhi-germination-detail',
  templateUrl: './germination-detail.component.html',
})
export class GerminationDetailComponent implements OnInit {
  germination: IGermination | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ germination }) => {
      this.germination = germination;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
