import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IThorne } from '../thorne.model';

@Component({
  selector: 'jhi-thorne-detail',
  templateUrl: './thorne-detail.component.html',
})
export class ThorneDetailComponent implements OnInit {
  thorne: IThorne | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ thorne }) => {
      this.thorne = thorne;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
