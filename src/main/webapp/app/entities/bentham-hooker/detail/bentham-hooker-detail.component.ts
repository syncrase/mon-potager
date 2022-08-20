import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IBenthamHooker} from '../bentham-hooker.model';

@Component({
  selector: 'jhi-bentham-hooker-detail',
  templateUrl: './bentham-hooker-detail.component.html',
})
export class BenthamHookerDetailComponent implements OnInit {
  benthamHooker: IBenthamHooker | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ benthamHooker }) => {
      this.benthamHooker = benthamHooker;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
