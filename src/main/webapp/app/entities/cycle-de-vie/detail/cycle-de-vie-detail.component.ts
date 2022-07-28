import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICycleDeVie } from '../cycle-de-vie.model';

@Component({
  selector: 'jhi-cycle-de-vie-detail',
  templateUrl: './cycle-de-vie-detail.component.html',
})
export class CycleDeVieDetailComponent implements OnInit {
  cycleDeVie: ICycleDeVie | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cycleDeVie }) => {
      this.cycleDeVie = cycleDeVie;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
