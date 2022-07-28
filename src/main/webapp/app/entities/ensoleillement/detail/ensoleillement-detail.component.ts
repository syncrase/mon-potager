import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEnsoleillement } from '../ensoleillement.model';

@Component({
  selector: 'jhi-ensoleillement-detail',
  templateUrl: './ensoleillement-detail.component.html',
})
export class EnsoleillementDetailComponent implements OnInit {
  ensoleillement: IEnsoleillement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ensoleillement }) => {
      this.ensoleillement = ensoleillement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
