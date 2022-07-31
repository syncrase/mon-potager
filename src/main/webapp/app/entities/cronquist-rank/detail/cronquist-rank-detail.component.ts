import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICronquistRank } from '../cronquist-rank.model';

@Component({
  selector: 'jhi-cronquist-rank-detail',
  templateUrl: './cronquist-rank-detail.component.html',
})
export class CronquistRankDetailComponent implements OnInit {
  cronquistRank: ICronquistRank | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cronquistRank }) => {
      this.cronquistRank = cronquistRank;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
