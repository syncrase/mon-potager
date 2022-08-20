import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUrl } from '../url.model';

@Component({
  selector: 'jhi-url-detail',
  templateUrl: './url-detail.component.html',
})
export class UrlDetailComponent implements OnInit {
  url: IUrl | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ url }) => {
      this.url = url;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
