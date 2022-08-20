import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {ICandolle} from '../candolle.model';

@Component({
  selector: 'jhi-candolle-detail',
  templateUrl: './candolle-detail.component.html',
})
export class CandolleDetailComponent implements OnInit {
  candolle: ICandolle | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ candolle }) => {
      this.candolle = candolle;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
