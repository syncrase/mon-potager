import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReference } from '../reference.model';

@Component({
  selector: 'jhi-reference-detail',
  templateUrl: './reference-detail.component.html',
})
export class ReferenceDetailComponent implements OnInit {
  reference: IReference | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reference }) => {
      this.reference = reference;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
