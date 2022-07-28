import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStrate } from '../strate.model';

@Component({
  selector: 'jhi-strate-detail',
  templateUrl: './strate-detail.component.html',
})
export class StrateDetailComponent implements OnInit {
  strate: IStrate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ strate }) => {
      this.strate = strate;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
