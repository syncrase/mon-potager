import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IDahlgren} from '../dahlgren.model';

@Component({
  selector: 'jhi-dahlgren-detail',
  templateUrl: './dahlgren-detail.component.html',
})
export class DahlgrenDetailComponent implements OnInit {
  dahlgren: IDahlgren | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dahlgren }) => {
      this.dahlgren = dahlgren;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
