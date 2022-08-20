import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITakhtajan } from '../takhtajan.model';

@Component({
  selector: 'jhi-takhtajan-detail',
  templateUrl: './takhtajan-detail.component.html',
})
export class TakhtajanDetailComponent implements OnInit {
  takhtajan: ITakhtajan | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ takhtajan }) => {
      this.takhtajan = takhtajan;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
