import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEngler } from '../engler.model';

@Component({
  selector: 'jhi-engler-detail',
  templateUrl: './engler-detail.component.html',
})
export class EnglerDetailComponent implements OnInit {
  engler: IEngler | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ engler }) => {
      this.engler = engler;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
