import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {IClassification} from '../classification.model';

@Component({
  selector: 'jhi-classification-detail',
  templateUrl: './classification-detail.component.html',
})
export class ClassificationDetailComponent implements OnInit {
  classification: IClassification | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classification }) => {
      this.classification = classification;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
