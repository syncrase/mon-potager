import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PeriodeAnneeDetailComponent } from './periode-annee-detail.component';

describe('PeriodeAnnee Management Detail Component', () => {
  let comp: PeriodeAnneeDetailComponent;
  let fixture: ComponentFixture<PeriodeAnneeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PeriodeAnneeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ periodeAnnee: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PeriodeAnneeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PeriodeAnneeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load periodeAnnee on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.periodeAnnee).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
