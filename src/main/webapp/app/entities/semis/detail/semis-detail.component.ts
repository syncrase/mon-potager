import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISemis } from '../semis.model';

@Component({
  selector: 'jhi-semis-detail',
  templateUrl: './semis-detail.component.html',
})
export class SemisDetailComponent implements OnInit {
  semis: ISemis | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ semis }) => {
      this.semis = semis;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
