import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IAPG} from '../apg.model';

@Component({
  selector: 'jhi-apg-detail',
  templateUrl: './apg-detail.component.html',
})
export class APGDetailComponent implements OnInit {
  aPG: IAPG | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ aPG }) => {
      this.aPG = aPG;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
