import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FeuillageDetailComponent } from './feuillage-detail.component';

describe('Feuillage Management Detail Component', () => {
  let comp: FeuillageDetailComponent;
  let fixture: ComponentFixture<FeuillageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeuillageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ feuillage: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FeuillageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FeuillageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load feuillage on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.feuillage).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
