import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GerminationDetailComponent } from './germination-detail.component';

describe('Germination Management Detail Component', () => {
  let comp: GerminationDetailComponent;
  let fixture: ComponentFixture<GerminationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GerminationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ germination: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GerminationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GerminationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load germination on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.germination).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
