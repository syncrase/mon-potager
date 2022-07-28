import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISol } from '../sol.model';

@Component({
  selector: 'jhi-sol-detail',
  templateUrl: './sol-detail.component.html',
})
export class SolDetailComponent implements OnInit {
  sol: ISol | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sol }) => {
      this.sol = sol;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
