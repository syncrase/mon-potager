import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRacine } from '../racine.model';

@Component({
  selector: 'jhi-racine-detail',
  templateUrl: './racine-detail.component.html',
})
export class RacineDetailComponent implements OnInit {
  racine: IRacine | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ racine }) => {
      this.racine = racine;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
